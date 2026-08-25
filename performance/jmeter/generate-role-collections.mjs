#!/usr/bin/env node

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDir, '../..');
const spec = JSON.parse(fs.readFileSync(path.join(projectRoot, 'postman', 'KnstoreApi.json'), 'utf8'));
const outputPath = path.join(scriptDir, 'knstore-role-collections.jmx');
const methods = ['get', 'post', 'put', 'patch', 'delete'];

const xml = value => String(value ?? '')
  .replaceAll('&', '&amp;')
  .replaceAll('<', '&lt;')
  .replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;')
  .replaceAll("'", '&apos;');

const v = name => `\${${name}}`;
const p = (name, fallback) => `\${__P(${name},${fallback})}`;

const roles = [
  {name: 'ROLE_ADMIN - jmeter_admin', token: 'jwtAdmin', user: 'adminUser', kind: 'admin'},
  {name: 'ROLE_MANAGER - jmeter_manager', token: 'jwtManager', user: 'managerUser', kind: 'manager'},
  {name: 'ROLE_CLIENTE_A - jmeter_cliente_a', token: 'jwtClientA', user: 'clientAUser', kind: 'client'},
  {name: 'ROLE_CLIENTE_B - jmeter_cliente_b', token: 'jwtClientB', user: 'clientBUser', kind: 'client'},
  {name: 'ROLE_USER - jmeter_user', token: 'jwtUser', user: 'userUser', kind: 'user'},
];

function queryValue(parameter) {
  if (parameter.name === 'q') return 'zapato';
  if (parameter.name === 'page') return '0';
  if (parameter.name === 'size') return '3';
  if (parameter.name === 'eagerload') return 'true';
  if (parameter.name === 'key') return 'NOT_FOUND';
  if (parameter.schema?.default !== undefined) return String(parameter.schema.default);
  if (parameter.required) return 'NOT_FOUND';
  return null;
}

function requestPath(apiPath, operation) {
  const parameters = [...(spec.paths[apiPath]?.parameters || []), ...(operation.parameters || [])];
  const pathValue = apiPath.replace(/\{([^}]+)\}/g, (_, name) => name === 'login' ? v('login') : name === 'slug' ? v('slug') : v('id'));
  const query = parameters.filter(parameter => parameter.in === 'query')
    .map(parameter => [parameter.name, queryValue(parameter)])
    .filter(([, value]) => value !== null)
    .map(([name, value]) => `${encodeURIComponent(name)}=${value}`);
  return `${pathValue}${query.length ? `?${query.join('&')}` : ''}`;
}

function hasPathParameter(apiPath) {
  return /\{[^}]+\}/.test(apiPath);
}

function hasMissingRequiredQuery(apiPath, operation) {
  const parameters = [...(spec.paths[apiPath]?.parameters || []), ...(operation.parameters || [])];
  return parameters.some(parameter => parameter.in === 'query' && parameter.required && queryValue(parameter) === 'NOT_FOUND');
}

function isPublicGet(apiPath) {
  return ['/api/categorias', '/api/subcategorias', '/api/productos', '/api/marcas', '/api/users', '/api/tipo-documentos'].includes(apiPath);
}

function isAdminOnlyGet(apiPath) {
  return apiPath === '/api/admin/users' || apiPath.startsWith('/api/authorities') || ['/api/producto-precios', '/api/producto-inventarios', '/api/producto-imagens', '/api/categoria-ivas', '/api/etiqueta-productos'].includes(apiPath);
}

function includeForRole(role, apiPath, method) {
  if (role.kind === 'admin') return true;
  if (apiPath.startsWith('/api/admin/') || apiPath === '/api/authorities' || apiPath.startsWith('/api/authorities/')) return role.kind !== 'client' && role.kind !== 'user';
  if (role.kind === 'manager') return true;
  if (role.kind === 'user') return method === 'get';
  if (role.kind === 'client') {
    return method === 'get' || ['/api/account', '/api/register', '/api/direccions', '/api/cuentas', '/api/carritos', '/api/item-carritos', '/api/pedidos', '/api/pagos', '/api/envios', '/api/facturas'].some(prefix => apiPath.startsWith(prefix));
  }
  return false;
}

function enabledForRole(role, apiPath, method, operation) {
  if (!includeForRole(role, apiPath, method)) return false;
  if (apiPath === '/api/authenticate' && method === 'post') return false;
  if (method !== 'get') return false;
  return !hasPathParameter(apiPath) && !hasMissingRequiredQuery(apiPath, operation);
}

function expectedForRole(role, apiPath, method) {
  if (apiPath === '/api/authenticate' && method === 'get') return '204';
  if (role.kind === 'user' && !isPublicGet(apiPath) && apiPath !== '/api/account') return '403';
  if (role.kind === 'manager' && (apiPath.startsWith('/api/admin/') || apiPath.startsWith('/api/authorities'))) return '403';
  if ((role.kind === 'client' || role.kind === 'user') && isAdminOnlyGet(apiPath)) return '403';
  if (role.kind === 'client' && (apiPath.startsWith('/api/admin/') || apiPath.startsWith('/api/authorities/'))) return '403';
  return '200';
}

function bodyFor(apiPath, method) {
  if (apiPath === '/api/authenticate' && method === 'post') return `{"username":"${v('activeUser')}","password":"${v('password')}","rememberMe":true}`;
  return '{}';
}

function headers(token, includeAuth, method) {
  const values = [['Accept', 'application/json']];
  if (includeAuth) values.push(['Authorization', `Bearer ${v(token)}`]);
  if (method !== 'get' && method !== 'delete') values.push(['Content-Type', 'application/json']);
  return `<HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="Headers" enabled="true"><collectionProp name="HeaderManager.headers">${values.map(([name, value]) => `<elementProp name="" elementType="Header"><stringProp name="Header.name">${xml(name)}</stringProp><stringProp name="Header.value">${xml(value)}</stringProp></elementProp>`).join('')}</collectionProp></HeaderManager><hashTree/>`;
}

function expectedHandler(expected) {
  if (['200', '201', '202', '204'].includes(String(expected))) {
    return `<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Expected HTTP ${expected}" enabled="true"><collectionProp name="Asserion.test_strings"><stringProp name="">${expected}</stringProp></collectionProp><stringProp name="Assertion.test_field">Assertion.response_code</stringProp><intProp name="Assertion.test_type">8</intProp></ResponseAssertion><hashTree/>`;
  }
  return `<JSR223PostProcessor guiclass="TestBeanGUI" testclass="JSR223PostProcessor" testname="Accept expected HTTP ${expected}" enabled="true"><stringProp name="cacheKey">true</stringProp><stringProp name="filename"></stringProp><stringProp name="parameters"></stringProp><stringProp name="scriptLanguage">groovy</stringProp><stringProp name="script">if (prev.getResponseCode() == '${expected}') { prev.setSuccessful(true) } else { prev.setSuccessful(false); prev.setResponseMessage('Expected ${expected}, received ' + prev.getResponseCode()) }</stringProp></JSR223PostProcessor><hashTree/>`;
}

function sampler(role, apiPath, method, operation, index) {
  const enabled = enabledForRole(role, apiPath, method, operation);
  const expected = expectedForRole(role, apiPath, method);
  const includeAuth = !(apiPath === '/api/authenticate' && method === 'post');
  const operationId = operation.operationId || `${method}-${apiPath}`;
  const body = method !== 'get' && method !== 'delete';
  const args = body
    ? `<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"><elementProp name="" elementType="HTTPArgument"><boolProp name="HTTPArgument.always_encode">false</boolProp><stringProp name="Argument.value">${xml(bodyFor(apiPath, method))}</stringProp><stringProp name="Argument.metadata">=</stringProp><boolProp name="HTTPArgument.use_equals">true</boolProp><stringProp name="Argument.name"></stringProp></elementProp></collectionProp></elementProp>`
    : '<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp>';
  const assertion = enabled
    ? expectedHandler(expected)
    : '';
  return `<HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="${xml(`${String(index).padStart(3, '0')} ${method.toUpperCase()} ${apiPath} :: ${operationId}`)}" enabled="${enabled}">${args}<stringProp name="HTTPSampler.domain">${v('host')}</stringProp><stringProp name="HTTPSampler.port">${v('port')}</stringProp><stringProp name="HTTPSampler.protocol">${v('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><stringProp name="HTTPSampler.path">${xml(requestPath(apiPath, operation))}</stringProp><stringProp name="HTTPSampler.method">${method.toUpperCase()}</stringProp><boolProp name="HTTPSampler.follow_redirects">true</boolProp><boolProp name="HTTPSampler.auto_redirects">false</boolProp><boolProp name="HTTPSampler.use_keepalive">true</boolProp><boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp><boolProp name="HTTPSampler.postBodyRaw">${body}</boolProp></HTTPSamplerProxy><hashTree>${headers(role.token, includeAuth, method)}${assertion}</hashTree>`;
}

function listener(guiClass, name, responseData) {
  return `<ResultCollector guiclass="${guiClass}" testclass="ResultCollector" testname="${name}" enabled="true"><boolProp name="ResultCollector.error_logging">false</boolProp><objProp><name>saveConfig</name><value class="SampleSaveConfiguration"><time>true</time><latency>true</latency><timestamp>true</timestamp><success>true</success><label>true</label><code>true</code><message>true</message><threadName>true</threadName><dataType>true</dataType><encoding>false</encoding><assertions>true</assertions><subresults>true</subresults><responseData>${responseData}</responseData><samplerData>${responseData}</samplerData><xml>true</xml><fieldNames>true</fieldNames><responseHeaders>${responseData}</responseHeaders><requestHeaders>${responseData}</requestHeaders><responseDataOnError>true</responseDataOnError><saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage><assertionsResultsToSave>0</assertionsResultsToSave><bytes>true</bytes><sentBytes>true</sentBytes><url>true</url><threadCounts>true</threadCounts><sampleCount>true</sampleCount></value></objProp><stringProp name="filename"></stringProp></ResultCollector><hashTree/>`;
}

function listeners() {
  return `${listener('SummaryReport', 'Summary Report', 'false')}${listener('RespTimeGraphVisualizer', 'Response Time Graph', 'false')}${listener('ViewResultsFullVisualizer', 'View Results Tree', 'true')}`;
}

function auth(role) {
  return `<GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="00 - AUTH ${role.name}" enabled="true"/><hashTree><HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="00.01 POST /api/authenticate" enabled="true"><elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"><elementProp name="" elementType="HTTPArgument"><boolProp name="HTTPArgument.always_encode">false</boolProp><stringProp name="Argument.value">{"username":"${v('activeUser')}","password":"${v('password')}","rememberMe":true}</stringProp><stringProp name="Argument.metadata">=</stringProp><boolProp name="HTTPArgument.use_equals">true</boolProp><stringProp name="Argument.name"></stringProp></elementProp></collectionProp></elementProp><stringProp name="HTTPSampler.domain">${v('host')}</stringProp><stringProp name="HTTPSampler.port">${v('port')}</stringProp><stringProp name="HTTPSampler.protocol">${v('protocol')}</stringProp><stringProp name="HTTPSampler.path">/api/authenticate</stringProp><stringProp name="HTTPSampler.method">POST</stringProp><boolProp name="HTTPSampler.follow_redirects">true</boolProp><boolProp name="HTTPSampler.auto_redirects">false</boolProp><boolProp name="HTTPSampler.use_keepalive">true</boolProp><boolProp name="HTTPSampler.postBodyRaw">true</boolProp></HTTPSamplerProxy><hashTree>${headers(role.token, false, 'post')}<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Expected HTTP 200" enabled="true"><collectionProp name="Asserion.test_strings"><stringProp name="">200</stringProp></collectionProp><stringProp name="Assertion.test_field">Assertion.response_code</stringProp><intProp name="Assertion.test_type">8</intProp></ResponseAssertion><hashTree/><JSONPostProcessor guiclass="JSONPostProcessorGui" testclass="JSONPostProcessor" testname="Extract JWT" enabled="true"><stringProp name="JSONPostProcessor.referenceNames">${role.token}</stringProp><stringProp name="JSONPostProcessor.jsonPathExprs">$.id_token</stringProp><stringProp name="JSONPostProcessor.match_numbers">1</stringProp><stringProp name="JSONPostProcessor.defaultValues"></stringProp></JSONPostProcessor><hashTree/></hashTree></hashTree>`;
}

function roleVariables() {
  return `<elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="Variables de roles" enabled="true"><collectionProp name="Arguments.arguments"><elementProp name="protocol" elementType="Argument"><stringProp name="Argument.name">protocol</stringProp><stringProp name="Argument.value">${p('protocol', 'https')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="host" elementType="Argument"><stringProp name="Argument.name">host</stringProp><stringProp name="Argument.value">${p('host', 'app.knstore.duckdns.org')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="port" elementType="Argument"><stringProp name="Argument.name">port</stringProp><stringProp name="Argument.value">${p('port', '')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="password" elementType="Argument"><stringProp name="Argument.name">password</stringProp><stringProp name="Argument.value">${p('password', '123456')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="adminUser" elementType="Argument"><stringProp name="Argument.name">adminUser</stringProp><stringProp name="Argument.value">${p('adminUser', 'jmeter_admin')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="managerUser" elementType="Argument"><stringProp name="Argument.name">managerUser</stringProp><stringProp name="Argument.value">${p('managerUser', 'jmeter_manager')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="clientAUser" elementType="Argument"><stringProp name="Argument.name">clientAUser</stringProp><stringProp name="Argument.value">${p('clientAUser', 'jmeter_cliente_a')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="clientBUser" elementType="Argument"><stringProp name="Argument.name">clientBUser</stringProp><stringProp name="Argument.value">${p('clientBUser', 'jmeter_cliente_b')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="userUser" elementType="Argument"><stringProp name="Argument.name">userUser</stringProp><stringProp name="Argument.value">${p('userUser', 'jmeter_user')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="activeUser" elementType="Argument"><stringProp name="Argument.name">activeUser</stringProp><stringProp name="Argument.value"></stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp></collectionProp></elementProp>`;
}

const groups = roles.map((role, roleIndex) => {
  const methodGroups = methods.map(method => {
    const operations = Object.entries(spec.paths).flatMap(([apiPath, pathItem]) => Object.entries(pathItem).filter(([candidate]) => candidate === method).map(([, operation]) => [apiPath, operation]));
    const selected = operations.filter(([apiPath]) => includeForRole(role, apiPath, method));
    const samples = selected.map(([apiPath, operation], index) => sampler(role, apiPath, method, operation, index + 1)).join('');
    return `<GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="${method.toUpperCase()} - ${selected.length} operaciones" enabled="true"/><hashTree>${samples}</hashTree>`;
  }).join('');
  const userValue = v(role.user);
  return `<ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="${xml(role.name)}" enabled="true"><stringProp name="ThreadGroup.on_sample_error">continue</stringProp><elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Una iteracion" enabled="true"><boolProp name="LoopController.continue_forever">false</boolProp><stringProp name="LoopController.loops">1</stringProp></elementProp><stringProp name="ThreadGroup.num_threads">1</stringProp><stringProp name="ThreadGroup.ramp_time">1</stringProp><longProp name="ThreadGroup.start_time">0</longProp><longProp name="ThreadGroup.end_time">0</longProp><boolProp name="ThreadGroup.scheduler">false</boolProp><stringProp name="ThreadGroup.duration"></stringProp><stringProp name="ThreadGroup.delay"></stringProp><boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp></ThreadGroup><hashTree><ConfigTestElement guiclass="HttpDefaultsGui" testclass="ConfigTestElement" testname="HTTP Request Defaults" enabled="true"><stringProp name="HTTPSampler.domain">${v('host')}</stringProp><stringProp name="HTTPSampler.port">${v('port')}</stringProp><stringProp name="HTTPSampler.protocol">${v('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp><stringProp name="HTTPSampler.connect_timeout">15000</stringProp><stringProp name="HTTPSampler.response_timeout">30000</stringProp></ConfigTestElement><hashTree/><CookieManager guiclass="CookiePanel" testclass="CookieManager" testname="Cookie Manager" enabled="true"><collectionProp name="CookieManager.cookies"/><boolProp name="CookieManager.clearEachIteration">false</boolProp><boolProp name="CookieManager.controlledByThreadGroup">false</boolProp></CookieManager><hashTree/><JSR223PreProcessor guiclass="TestBeanGUI" testclass="JSR223PreProcessor" testname="Set active role user" enabled="true"><stringProp name="cacheKey">true</stringProp><stringProp name="filename"></stringProp><stringProp name="parameters"></stringProp><stringProp name="scriptLanguage">groovy</stringProp><stringProp name="script">vars.put('activeUser', vars.get('${role.user}'))</stringProp></JSR223PreProcessor><hashTree/>${auth(role)}${methodGroups}${listeners()}</hashTree>`;
}).join('');

const plan = `<?xml version="1.0" encoding="UTF-8"?><jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3"><hashTree><TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="KN-Store - Colecciones por rol" enabled="true"><stringProp name="TestPlan.comments">Colecciones GET POST PUT PATCH DELETE organizadas por rol desde Postman.</stringProp><boolProp name="TestPlan.functional_mode">false</boolProp><boolProp name="TestPlan.serialize_threadgroups">false</boolProp>${roleVariables()}<stringProp name="TestPlan.user_define_classpath"></stringProp></TestPlan><hashTree>${groups}</hashTree></hashTree></jmeterTestPlan>\n`;

fs.writeFileSync(outputPath, plan, 'utf8');
console.log(`Generated role collection plan at ${outputPath}`);
console.log(`Roles: ${roles.length}; routes: ${Object.keys(spec.paths).length}; operations: ${Object.values(spec.paths).flatMap(pathItem => Object.keys(pathItem).filter(method => methods.includes(method))).length}`);
