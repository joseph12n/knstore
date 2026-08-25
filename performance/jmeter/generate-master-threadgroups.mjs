#!/usr/bin/env node

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDir, '../..');
const spec = JSON.parse(fs.readFileSync(path.join(projectRoot, 'postman', 'KnstoreApi.json'), 'utf8'));
const outputPath = path.join(scriptDir, 'knstore-backend-sondeo.jmx');
const methods = new Set(['get', 'post', 'put', 'patch', 'delete', 'head', 'options', 'trace']);

const xml = value => String(value ?? '')
  .replaceAll('&', '&amp;')
  .replaceAll('<', '&lt;')
  .replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;')
  .replaceAll("'", '&apos;');

const variable = name => `\${${name}}`;
const property = (name, fallback) => `\${__P(${name},${fallback})}`;

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
  const pathValue = apiPath.replace(/\{([^}]+)\}/g, (_, name) => name === 'login' ? variable('login') : name === 'slug' ? variable('slug') : variable('id'));
  const query = parameters
    .filter(parameter => parameter.in === 'query')
    .map(parameter => [parameter.name, queryValue(parameter)])
    .filter(([, value]) => value !== null)
    .map(([name, value]) => `${encodeURIComponent(name)}=${value}`);
  return `${pathValue}${query.length ? `?${query.join('&')}` : ''}`;
}

function safeByDefault(apiPath, method, operation) {
  if (apiPath === '/api/authenticate' && method === 'get') return true;
  if (method !== 'get' || /\{[^}]+\}/.test(apiPath)) return false;
  const parameters = [...(spec.paths[apiPath]?.parameters || []), ...(operation.parameters || [])];
  return !parameters.some(parameter => parameter.in === 'query' && parameter.required && queryValue(parameter) === 'NOT_FOUND');
}

function bodyFor(apiPath, method) {
  if (apiPath === '/api/authenticate' && method === 'post') {
    return `{
  "username": "${variable('username')}",
  "password": "${variable('password')}",
  "rememberMe": true
}`;
  }
  return '{}';
}

function bodyArguments(body) {
  return `<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"><elementProp name="" elementType="HTTPArgument"><boolProp name="HTTPArgument.always_encode">false</boolProp><stringProp name="Argument.value">${xml(body)}</stringProp><stringProp name="Argument.metadata">=</stringProp><boolProp name="HTTPArgument.use_equals">true</boolProp><stringProp name="Argument.name"></stringProp></elementProp></collectionProp></elementProp>`;
}

function headers(method, needsAuth) {
  const values = [['Accept', 'application/json']];
  if (needsAuth) values.push(['Authorization', `Bearer ${variable('jwtToken')}`]);
  if (method !== 'get' && method !== 'head') values.push(['Content-Type', 'application/json']);
  return `<HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="Request headers" enabled="true"><collectionProp name="HeaderManager.headers">${values.map(([name, value]) => `<elementProp name="" elementType="Header"><stringProp name="Header.name">${xml(name)}</stringProp><stringProp name="Header.value">${xml(value)}</stringProp></elementProp>`).join('')}</collectionProp></HeaderManager><hashTree/>`;
}

function sampler(apiPath, method, operation, index) {
  const enabled = safeByDefault(apiPath, method, operation);
  const needsAuth = !(apiPath === '/api/authenticate' && method === 'post');
  const operationId = operation.operationId || `${method}-${apiPath}`;
  const body = method !== 'get' && method !== 'head';
  const expected = apiPath === '/api/authenticate' && method === 'get' ? '204' : '200';
  const assertion = enabled ? `<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Expected HTTP ${expected}" enabled="true"><collectionProp name="Asserion.test_strings"><stringProp name="">${expected}</stringProp></collectionProp><stringProp name="Assertion.test_field">Assertion.response_code</stringProp><intProp name="Assertion.test_type">8</intProp></ResponseAssertion><hashTree/>` : '';
  return `<HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="${xml(`${String(index).padStart(3, '0')} ${method.toUpperCase()} ${apiPath} :: ${operationId}`)}" enabled="${enabled}">${body ? bodyArguments(bodyFor(apiPath, method)) : '<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp>'}<stringProp name="HTTPSampler.domain">${variable('host')}</stringProp><stringProp name="HTTPSampler.port">${variable('port')}</stringProp><stringProp name="HTTPSampler.protocol">${variable('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><stringProp name="HTTPSampler.path">${xml(requestPath(apiPath, operation))}</stringProp><stringProp name="HTTPSampler.method">${method.toUpperCase()}</stringProp><boolProp name="HTTPSampler.follow_redirects">true</boolProp><boolProp name="HTTPSampler.auto_redirects">false</boolProp><boolProp name="HTTPSampler.use_keepalive">true</boolProp><boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp><boolProp name="HTTPSampler.postBodyRaw">${body}</boolProp></HTTPSamplerProxy><hashTree>${headers(method, needsAuth)}${assertion}</hashTree>`;
}

function authSetup() {
  return `<GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="00 - Autenticacion" enabled="true"/><hashTree><HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="00.01 POST /api/authenticate :: authorize" enabled="true">${bodyArguments(bodyFor('/api/authenticate', 'post'))}<stringProp name="HTTPSampler.domain">${variable('host')}</stringProp><stringProp name="HTTPSampler.port">${variable('port')}</stringProp><stringProp name="HTTPSampler.protocol">${variable('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><stringProp name="HTTPSampler.path">/api/authenticate</stringProp><stringProp name="HTTPSampler.method">POST</stringProp><boolProp name="HTTPSampler.follow_redirects">true</boolProp><boolProp name="HTTPSampler.auto_redirects">false</boolProp><boolProp name="HTTPSampler.use_keepalive">true</boolProp><boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp><boolProp name="HTTPSampler.postBodyRaw">true</boolProp></HTTPSamplerProxy><hashTree>${headers('post', false)}<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Expected HTTP 200" enabled="true"><collectionProp name="Asserion.test_strings"><stringProp name="">200</stringProp></collectionProp><stringProp name="Assertion.test_field">Assertion.response_code</stringProp><intProp name="Assertion.test_type">8</intProp></ResponseAssertion><hashTree/><JSONPostProcessor guiclass="JSONPostProcessorGui" testclass="JSONPostProcessor" testname="Extraer JWT" enabled="true"><stringProp name="JSONPostProcessor.referenceNames">jwtToken</stringProp><stringProp name="JSONPostProcessor.jsonPathExprs">$.id_token</stringProp><stringProp name="JSONPostProcessor.match_numbers">1</stringProp><stringProp name="JSONPostProcessor.defaultValues"></stringProp></JSONPostProcessor><hashTree/></hashTree></hashTree>`;
}

function listener(guiclass, testName, includeResponseData) {
  return `<ResultCollector guiclass="${guiclass}" testclass="ResultCollector" testname="${testName}" enabled="true"><boolProp name="ResultCollector.error_logging">false</boolProp><objProp><name>saveConfig</name><value class="SampleSaveConfiguration"><time>true</time><latency>true</latency><timestamp>true</timestamp><success>true</success><label>true</label><code>true</code><message>true</message><threadName>true</threadName><dataType>true</dataType><encoding>false</encoding><assertions>true</assertions><subresults>true</subresults><responseData>${includeResponseData}</responseData><samplerData>${includeResponseData}</samplerData><xml>true</xml><fieldNames>true</fieldNames><responseHeaders>${includeResponseData}</responseHeaders><requestHeaders>${includeResponseData}</requestHeaders><responseDataOnError>true</responseDataOnError><saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage><assertionsResultsToSave>0</assertionsResultsToSave><bytes>true</bytes><sentBytes>true</sentBytes><url>true</url><threadCounts>true</threadCounts><sampleCount>true</sampleCount></value></objProp><stringProp name="filename"></stringProp></ResultCollector><hashTree/>`;
}

function listeners() {
  return `${listener('SummaryReport', 'Summary Report', 'false')}${listener('RespTimeGraphVisualizer', 'Response Time Graph', 'false')}${listener('ViewResultsFullVisualizer', 'View Results Tree', 'true')}`;
}

function variables() {
  return `<elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="Variables globales" enabled="true"><collectionProp name="Arguments.arguments"><elementProp name="protocol" elementType="Argument"><stringProp name="Argument.name">protocol</stringProp><stringProp name="Argument.value">${property('protocol', 'https')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="host" elementType="Argument"><stringProp name="Argument.name">host</stringProp><stringProp name="Argument.value">${property('host', 'app.knstore.duckdns.org')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="port" elementType="Argument"><stringProp name="Argument.name">port</stringProp><stringProp name="Argument.value">${property('port', '')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="username" elementType="Argument"><stringProp name="Argument.name">username</stringProp><stringProp name="Argument.value">${property('username', '')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="password" elementType="Argument"><stringProp name="Argument.name">password</stringProp><stringProp name="Argument.value">${property('password', '')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="jwtToken" elementType="Argument"><stringProp name="Argument.name">jwtToken</stringProp><stringProp name="Argument.value"></stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="id" elementType="Argument"><stringProp name="Argument.name">id</stringProp><stringProp name="Argument.value">NOT_FOUND</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="login" elementType="Argument"><stringProp name="Argument.name">login</stringProp><stringProp name="Argument.value">admin</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="slug" elementType="Argument"><stringProp name="Argument.name">slug</stringProp><stringProp name="Argument.value">nike-air-max-hombre-negro-38</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp></collectionProp></elementProp>`;
}

function threadGroup(apiPath, pathItem, index) {
  const operations = Object.entries(pathItem).filter(([method]) => methods.has(method));
  const samples = operations.map(([method, operation], operationIndex) => sampler(apiPath, method, operation, operationIndex + 1)).join('');
  return `<ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="API ${String(index).padStart(2, '0')} - ${xml(apiPath)}" enabled="true"><stringProp name="ThreadGroup.on_sample_error">continue</stringProp><elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Controlador de iteraciones" enabled="true"><boolProp name="LoopController.continue_forever">false</boolProp><stringProp name="LoopController.loops">${property('loops', '1')}</stringProp></elementProp><stringProp name="ThreadGroup.num_threads">${property('threads', '1')}</stringProp><stringProp name="ThreadGroup.ramp_time">${property('ramp', '1')}</stringProp><longProp name="ThreadGroup.start_time">0</longProp><longProp name="ThreadGroup.end_time">0</longProp><boolProp name="ThreadGroup.scheduler">false</boolProp><stringProp name="ThreadGroup.duration"></stringProp><stringProp name="ThreadGroup.delay"></stringProp><boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp></ThreadGroup><hashTree><ConfigTestElement guiclass="HttpDefaultsGui" testclass="ConfigTestElement" testname="HTTP Request Defaults" enabled="true"><stringProp name="HTTPSampler.domain">${variable('host')}</stringProp><stringProp name="HTTPSampler.port">${variable('port')}</stringProp><stringProp name="HTTPSampler.protocol">${variable('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp><stringProp name="HTTPSampler.connect_timeout">15000</stringProp><stringProp name="HTTPSampler.response_timeout">30000</stringProp></ConfigTestElement><hashTree/><CookieManager guiclass="CookiePanel" testclass="CookieManager" testname="Cookie Manager" enabled="true"><collectionProp name="CookieManager.cookies"/><boolProp name="CookieManager.clearEachIteration">false</boolProp><boolProp name="CookieManager.controlledByThreadGroup">false</boolProp></CookieManager><hashTree/>${authSetup()}<GenericController guiclass="LogicControllerGui" testclass="GenericController" testname="${xml(apiPath)}" enabled="true"/><hashTree>${samples}</hashTree>${listeners()}</hashTree>`;
}

const groups = Object.entries(spec.paths).map(([apiPath, pathItem], index) => threadGroup(apiPath, pathItem, index + 1)).join('');
const plan = `<?xml version="1.0" encoding="UTF-8"?><jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3"><hashTree><TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="KN-Store - Todas las APIs de Postman" enabled="true"><stringProp name="TestPlan.comments">Un Thread Group por cada ruta API de postman/KnstoreApi.json. Las escrituras quedan deshabilitadas.</stringProp><boolProp name="TestPlan.functional_mode">false</boolProp><boolProp name="TestPlan.serialize_threadgroups">false</boolProp>${variables()}<stringProp name="TestPlan.user_define_classpath"></stringProp></TestPlan><hashTree>${groups}</hashTree></hashTree></jmeterTestPlan>\n`;

fs.writeFileSync(outputPath, plan, 'utf8');
console.log(`Generated master with ${Object.keys(spec.paths).length} top-level Thread Groups`);
