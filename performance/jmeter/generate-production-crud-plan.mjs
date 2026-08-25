#!/usr/bin/env node

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const outputPath = path.join(scriptDir, 'knstore-production-crud-lifecycle.jmx');

const xml = value => String(value ?? '')
  .replaceAll('&', '&amp;')
  .replaceAll('<', '&lt;')
  .replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;')
  .replaceAll("'", '&apos;');

const v = name => `\${${name}}`;
const p = (name, fallback) => `\${__P(${name},${fallback})}`;

function headers(token, method, includeAuth = true) {
  const values = [
    ['Accept', 'application/json'],
  ];
  if (includeAuth) values.push(['Authorization', `Bearer ${v(token)}`]);
  if (method !== 'GET' && method !== 'DELETE') values.push(['Content-Type', 'application/json']);
  return `<HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="Headers" enabled="true"><collectionProp name="HeaderManager.headers">${values.map(([name, value]) => `<elementProp name="" elementType="Header"><stringProp name="Header.name">${xml(name)}</stringProp><stringProp name="Header.value">${xml(value)}</stringProp></elementProp>`).join('')}</collectionProp></HeaderManager><hashTree/>`;
}

function bodyArguments(body) {
  return `<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"><elementProp name="" elementType="HTTPArgument"><boolProp name="HTTPArgument.always_encode">false</boolProp><stringProp name="Argument.value">${xml(body)}</stringProp><stringProp name="Argument.metadata">=</stringProp><boolProp name="HTTPArgument.use_equals">true</boolProp><stringProp name="Argument.name"></stringProp></elementProp></collectionProp></elementProp>`;
}

function listener(guiclass, name, data) {
  return `<ResultCollector guiclass="${guiclass}" testclass="ResultCollector" testname="${name}" enabled="true"><boolProp name="ResultCollector.error_logging">false</boolProp><objProp><name>saveConfig</name><value class="SampleSaveConfiguration"><time>true</time><latency>true</latency><timestamp>true</timestamp><success>true</success><label>true</label><code>true</code><message>true</message><threadName>true</threadName><dataType>true</dataType><encoding>false</encoding><assertions>true</assertions><subresults>true</subresults><responseData>${data}</responseData><samplerData>${data}</samplerData><xml>true</xml><fieldNames>true</fieldNames><responseHeaders>${data}</responseHeaders><requestHeaders>${data}</requestHeaders><responseDataOnError>true</responseDataOnError><saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage><assertionsResultsToSave>0</assertionsResultsToSave><bytes>true</bytes><sentBytes>true</sentBytes><url>true</url><threadCounts>true</threadCounts><sampleCount>true</sampleCount></value></objProp><stringProp name="filename"></stringProp></ResultCollector><hashTree/>`;
}

function listeners() {
  return `${listener('SummaryReport', 'Summary Report', 'false')}${listener('RespTimeGraphVisualizer', 'Response Time Graph', 'false')}${listener('ViewResultsFullVisualizer', 'View Results Tree', 'true')}`;
}

function extractors(extract) {
  if (!extract) return '';
  const [reference, jsonPath] = extract;
  return `<JSONPostProcessor guiclass="JSONPostProcessorGui" testclass="JSONPostProcessor" testname="Extract ${reference}" enabled="true"><stringProp name="JSONPostProcessor.referenceNames">${reference}</stringProp><stringProp name="JSONPostProcessor.jsonPathExprs">${jsonPath}</stringProp><stringProp name="JSONPostProcessor.match_numbers">1</stringProp><stringProp name="JSONPostProcessor.defaultValues">NOT_FOUND</stringProp></JSONPostProcessor><hashTree/>`;
}

function expectedHandler(expected) {
  if (['200', '201', '202', '204'].includes(String(expected))) {
    return `<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Expected HTTP ${expected}" enabled="true"><collectionProp name="Asserion.test_strings"><stringProp name="">${expected}</stringProp></collectionProp><stringProp name="Assertion.test_field">Assertion.response_code</stringProp><intProp name="Assertion.test_type">8</intProp></ResponseAssertion><hashTree/>`;
  }
  return `<JSR223PostProcessor guiclass="TestBeanGUI" testclass="JSR223PostProcessor" testname="Accept expected HTTP ${expected}" enabled="true"><stringProp name="cacheKey">true</stringProp><stringProp name="filename"></stringProp><stringProp name="parameters"></stringProp><stringProp name="scriptLanguage">groovy</stringProp><stringProp name="script">if (prev.getResponseCode() == '${expected}') { prev.setSuccessful(true) } else { prev.setSuccessful(false); prev.setResponseMessage('Expected ${expected}, received ' + prev.getResponseCode()) }</stringProp></JSR223PostProcessor><hashTree/>`;
}

function request({label, method, path, token, body, expected = 200, extract}) {
  const hasBody = body !== undefined;
  const includeAuth = !label.startsWith('AUTH ');
  return `<HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="${xml(label)}" enabled="true">${hasBody ? bodyArguments(body) : '<elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp>'}<stringProp name="HTTPSampler.domain">${v('host')}</stringProp><stringProp name="HTTPSampler.port">${v('port')}</stringProp><stringProp name="HTTPSampler.protocol">${v('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><stringProp name="HTTPSampler.path">${xml(path)}</stringProp><stringProp name="HTTPSampler.method">${method}</stringProp><boolProp name="HTTPSampler.follow_redirects">true</boolProp><boolProp name="HTTPSampler.auto_redirects">false</boolProp><boolProp name="HTTPSampler.use_keepalive">true</boolProp><boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp><boolProp name="HTTPSampler.postBodyRaw">${hasBody}</boolProp></HTTPSamplerProxy><hashTree>${headers(token, method, includeAuth)}${expectedHandler(expected)}${extractors(extract)}</hashTree>`;
}

const steps = [];
const add = step => steps.push(request(step));

const roleDefaults = {adminUser: 'jmeter_admin', managerUser: 'jmeter_manager', clientAUser: 'jmeter_cliente_a', clientBUser: 'jmeter_cliente_b', userUser: 'jmeter_user'};
for (const [username, token] of [['adminUser', 'jwtAdmin'], ['managerUser', 'jwtManager'], ['clientAUser', 'jwtClientA'], ['clientBUser', 'jwtClientB'], ['userUser', 'jwtUser']]) {
  add({label: `AUTH ${username}`, method: 'POST', path: '/api/authenticate', token, body: `{"username":"${p(username, roleDefaults[username])}","password":"${p('password', '123456')}","rememberMe":true}`, expected: 200, extract: [token, '$.id_token']});
}

add({label: 'ADMIN GET reference categoria', method: 'GET', path: '/api/categorias?page=0&size=1', token: 'jwtAdmin', extract: ['baseCategoryId', '$[0].id']});
add({label: 'ADMIN GET reference subcategoria', method: 'GET', path: '/api/subcategorias?page=0&size=1&eagerload=true', token: 'jwtAdmin', extract: ['baseSubcategoryId', '$[0].id']});
add({label: 'ADMIN GET reference marca', method: 'GET', path: '/api/marcas', token: 'jwtAdmin', extract: ['baseBrandId', '$[0].id']});
add({label: 'ADMIN GET reference IVA', method: 'GET', path: '/api/categoria-ivas', token: 'jwtAdmin', extract: ['baseIvaId', '$[0].id']});
add({label: 'ADMIN GET reference tipo documento', method: 'GET', path: '/api/tipo-documentos', token: 'jwtAdmin', extract: ['baseDocumentTypeId', '$[0].id']});
add({label: 'ADMIN GET reference producto', method: 'GET', path: '/api/productos?page=0&size=1&eagerload=true', token: 'jwtAdmin', extract: ['baseProductId', '$[0].id']});

const categoryBody = `{"nombre":"JMETER TEST CATEGORIA ${v('suffix')}","slug":"jmeter-test-categoria-${v('suffix')}","descripcion":"Registro temporal","activo":true}`;
add({label: 'ADMIN POST categoria temporal', method: 'POST', path: '/api/categorias', token: 'jwtAdmin', body: categoryBody, expected: 201, extract: ['testCategoryId', '$.id']});
add({label: 'ADMIN PUT categoria temporal', method: 'PUT', path: '/api/categorias/${testCategoryId}', token: 'jwtAdmin', body: `{"id":"${v('testCategoryId')}","nombre":"JMETER UPDATED CATEGORIA ${v('suffix')}","slug":"jmeter-updated-categoria-${v('suffix')}","descripcion":"Actualizada","activo":true}`});

add({label: 'ADMIN POST subcategoria temporal', method: 'POST', path: '/api/subcategorias', token: 'jwtAdmin', body: `{"nombre":"JMETER TEST SUBCATEGORIA ${v('suffix')}","slug":"jmeter-test-subcategoria-${v('suffix')}","descripcion":"Registro temporal","activo":true,"categoria":{"id":"${v('testCategoryId')}"}}`, expected: 201, extract: ['testSubcategoryId', '$.id']});
add({label: 'ADMIN PUT subcategoria temporal', method: 'PUT', path: '/api/subcategorias/${testSubcategoryId}', token: 'jwtAdmin', body: `{"id":"${v('testSubcategoryId')}","nombre":"JMETER UPDATED SUBCATEGORIA ${v('suffix')}","slug":"jmeter-updated-subcategoria-${v('suffix')}","activo":true,"categoria":{"id":"${v('testCategoryId')}"}}`});

add({label: 'ADMIN POST categoria IVA temporal', method: 'POST', path: '/api/categoria-ivas', token: 'jwtAdmin', body: `{"nombre":"JMETER IVA ${v('suffix')}","porcentaje":5,"estado":"ACTIVO"}`, expected: 201, extract: ['testIvaId', '$.id']});
add({label: 'ADMIN PUT categoria IVA temporal', method: 'PUT', path: '/api/categoria-ivas/${testIvaId}', token: 'jwtAdmin', body: `{"id":"${v('testIvaId')}","nombre":"JMETER IVA UPDATED ${v('suffix')}","porcentaje":10,"estado":"ACTIVO"}`});

add({label: 'ADMIN POST marca temporal', method: 'POST', path: '/api/marcas', token: 'jwtAdmin', body: `{"nombre":"JMETER MARCA ${v('suffix')}","slug":"jmeter-marca-${v('suffix')}"}`, expected: 201, extract: ['testBrandId', '$.id']});
add({label: 'ADMIN PUT marca temporal', method: 'PUT', path: '/api/marcas/${testBrandId}', token: 'jwtAdmin', body: `{"id":"${v('testBrandId')}","nombre":"JMETER MARCA UPDATED ${v('suffix')}","slug":"jmeter-marca-updated-${v('suffix')}"}`});

add({label: 'ADMIN POST tipo documento temporal', method: 'POST', path: '/api/tipo-documentos', token: 'jwtAdmin', body: `{"sigla":"JM${v('suffix')}","nombreTipo":"JMETER TEST DOCUMENTO ${v('suffix')}","estado":"ACTIVO"}`, expected: 201, extract: ['testDocumentTypeId', '$.id']});
add({label: 'ADMIN PUT tipo documento temporal', method: 'PUT', path: '/api/tipo-documentos/${testDocumentTypeId}', token: 'jwtAdmin', body: `{"id":"${v('testDocumentTypeId')}","sigla":"JU${v('suffix')}","nombreTipo":"JMETER UPDATED DOCUMENTO ${v('suffix')}","estado":"ACTIVO"}`});

add({label: 'ADMIN POST producto temporal', method: 'POST', path: '/api/productos', token: 'jwtAdmin', body: `{"nombre":"JMETER PRODUCTO ${v('suffix')}","slug":"jmeter-producto-${v('suffix')}","sku":"JMETER-${v('suffix')}","referencia":"JMETER-${v('suffix')}","color":"Negro","talla":"40","unidadMedida":"Par","descripcion":"Registro temporal","destacado":false,"activo":true,"categoria":{"id":"${v('testCategoryId')}"},"subcategoria":{"id":"${v('testSubcategoryId')}"},"marca":{"id":"${v('testBrandId')}"},"categoriaIva":{"id":"${v('testIvaId')}"}}`, expected: 201, extract: ['testProductId', '$.id']});
add({label: 'ADMIN PUT producto temporal', method: 'PUT', path: '/api/productos/${testProductId}', token: 'jwtAdmin', body: `{"id":"${v('testProductId')}","nombre":"JMETER PRODUCTO UPDATED ${v('suffix')}","slug":"jmeter-producto-updated-${v('suffix')}","sku":"JMETER-UP-${v('suffix')}","referencia":"JMETER-UP-${v('suffix')}","color":"Blanco","talla":"41","unidadMedida":"Par","descripcion":"Actualizado","destacado":true,"activo":true,"categoria":{"id":"${v('testCategoryId')}"},"subcategoria":{"id":"${v('testSubcategoryId')}"},"marca":{"id":"${v('testBrandId')}"},"categoriaIva":{"id":"${v('testIvaId')}"}}`});

add({label: 'ADMIN POST precio temporal', method: 'POST', path: '/api/producto-precios', token: 'jwtAdmin', body: '{"precioCompra":10,"precioVenta":20,"precioAdicional":0,"ganancia":10}', expected: 201, extract: ['testPriceId', '$.id']});
add({label: 'ADMIN PUT precio temporal', method: 'PUT', path: '/api/producto-precios/${testPriceId}', token: 'jwtAdmin', body: `{"id":"${v('testPriceId')}","precioCompra":12,"precioVenta":24,"precioAdicional":1,"ganancia":12}`});
add({label: 'ADMIN POST inventario temporal', method: 'POST', path: '/api/producto-inventarios', token: 'jwtAdmin', body: '{"stock":10,"stockMinimo":2,"ubicacionBodega":"BODEGA_PRINCIPAL","garantiaMeses":1}', expected: 201, extract: ['testInventoryId', '$.id']});
add({label: 'ADMIN PUT inventario temporal', method: 'PUT', path: '/api/producto-inventarios/${testInventoryId}', token: 'jwtAdmin', body: `{"id":"${v('testInventoryId')}","stock":8,"stockMinimo":2,"ubicacionBodega":"BODEGA_SECUNDARIA","garantiaMeses":2}`});
add({label: 'ADMIN POST imagen temporal', method: 'POST', path: '/api/producto-imagens', token: 'jwtAdmin', body: `{"imagenAlt":"JMETER TEST","imagenContentType":"image/jpeg","esPrincipal":false,"imagenUrl":"https://example.com/jmeter-${v('suffix')}.jpg","producto":{"id":"${v('testProductId')}"}}`, expected: 201, extract: ['testImageId', '$.id']});
add({label: 'ADMIN PUT imagen temporal', method: 'PUT', path: '/api/producto-imagens/${testImageId}', token: 'jwtAdmin', body: `{"id":"${v('testImageId')}","imagenAlt":"JMETER UPDATED","imagenContentType":"image/png","esPrincipal":true,"imagenUrl":"https://example.com/jmeter-updated-${v('suffix')}.jpg","producto":{"id":"${v('testProductId')}"}}`});
add({label: 'ADMIN POST etiqueta temporal', method: 'POST', path: '/api/etiqueta-productos', token: 'jwtAdmin', body: `{"etiqueta":"JMETER_TEST_${v('suffix')}","producto":{"id":"${v('testProductId')}"}}`, expected: 201, extract: ['testLabelId', '$.id']});
add({label: 'ADMIN PUT etiqueta temporal', method: 'PUT', path: '/api/etiqueta-productos/${testLabelId}', token: 'jwtAdmin', body: `{"id":"${v('testLabelId')}","etiqueta":"JMETER_UPDATED_${v('suffix')}","producto":{"id":"${v('testProductId')}"}}`});

add({label: 'ADMIN POST usuario temporal', method: 'POST', path: '/api/admin/users', token: 'jwtAdmin', body: `{"login":"jmeter_crud_${v('suffix')}","firstName":"JMETER","lastName":"TEST","email":"jmeter-crud-${v('suffix')}@example.com","activated":true,"langKey":"es","authorities":["ROLE_USER"],"password":"123456"}`, expected: 201, extract: ['testUserId', '$.id']});
add({label: 'ADMIN PUT usuario temporal', method: 'PUT', path: '/api/admin/users', token: 'jwtAdmin', body: `{"id":"${v('testUserId')}","login":"jmeter_crud_${v('suffix')}","firstName":"JMETER UPDATED","lastName":"TEST","email":"jmeter-crud-${v('suffix')}@example.com","activated":true,"langKey":"es","authorities":["ROLE_USER"]}`});

add({label: 'ADMIN POST cuenta temporal', method: 'POST', path: '/api/cuentas', token: 'jwtAdmin', body: `{"numDocumento":"${v('testDocNumber')}","primerNombre":"JMETER","segundoNombre":"TEST","primerApellido":"CUENTA","segundoApellido":"TEMP","genero":"PREFIERO_NO_DECIR","fechaNacimiento":"1990-01-01","celular":"3001234567","telefono":"6011234567","activo":true,"user":{"id":"${v('testUserId')}","login":"jmeter_crud_${v('suffix')}"},"tipoDocumento":{"id":"${v('testDocumentTypeId')}"}}`, expected: 201, extract: ['testAccountId', '$.id']});
add({label: 'ADMIN PUT cuenta temporal', method: 'PUT', path: '/api/cuentas/${testAccountId}', token: 'jwtAdmin', body: `{"id":"${v('testAccountId')}","numDocumento":"${v('testDocNumber')}","primerNombre":"JMETER UPDATED","segundoNombre":"TEST","primerApellido":"CUENTA","segundoApellido":"TEMP","genero":"PREFIERO_NO_DECIR","fechaNacimiento":"1990-01-01","celular":"3001234567","telefono":"6011234567","activo":true,"user":{"id":"${v('testUserId')}","login":"jmeter_crud_${v('suffix')}"},"tipoDocumento":{"id":"${v('testDocumentTypeId')}"}}`});
add({label: 'ADMIN POST direccion temporal', method: 'POST', path: '/api/direccions', token: 'jwtAdmin', body: `{"direccion":"Calle JMETER 10 #20-30","barrio":"Centro","localidad":"Localidad","municipio":"Bogota","departamento":"Cundinamarca","activo":true,"telefonoContacto":"3001234567","destinatario":"JMETER TEST","codigoPostal":"110111","cuenta":{"id":"${v('testAccountId')}"}}`, expected: 201, extract: ['testAddressId', '$.id']});
add({label: 'ADMIN PUT direccion temporal', method: 'PUT', path: '/api/direccions/${testAddressId}', token: 'jwtAdmin', body: `{"id":"${v('testAddressId')}","direccion":"Calle JMETER UPDATED 10 #20-30","barrio":"Centro","localidad":"Localidad","municipio":"Bogota","departamento":"Cundinamarca","activo":true,"telefonoContacto":"3001234567","destinatario":"JMETER UPDATED","codigoPostal":"110111","cuenta":{"id":"${v('testAccountId')}"}}`});
add({label: 'ADMIN POST carrito temporal', method: 'POST', path: '/api/carritos', token: 'jwtAdmin', body: `{"subtotal":0,"cuenta":{"id":"${v('testAccountId')}"}}`, expected: 201, extract: ['testCartId', '$.id']});
add({label: 'ADMIN PUT carrito temporal', method: 'PUT', path: '/api/carritos/${testCartId}', token: 'jwtAdmin', body: `{"id":"${v('testCartId')}","subtotal":20,"cuenta":{"id":"${v('testAccountId')}"}}`});
add({label: 'ADMIN POST item carrito temporal', method: 'POST', path: '/api/item-carritos', token: 'jwtAdmin', body: `{"cantidad":1,"precioUnitario":20,"subtotal":20,"carrito":{"id":"${v('testCartId')}"},"producto":{"id":"${v('baseProductId')}"}}`, expected: 201, extract: ['testCartItemId', '$.id']});
add({label: 'ADMIN PUT item carrito temporal', method: 'PUT', path: '/api/item-carritos/${testCartItemId}', token: 'jwtAdmin', body: `{"id":"${v('testCartItemId')}","cantidad":2,"precioUnitario":20,"subtotal":40,"carrito":{"id":"${v('testCartId')}"},"producto":{"id":"${v('baseProductId')}"}}`});
add({label: 'ADMIN POST pedido temporal', method: 'POST', path: '/api/pedidos', token: 'jwtAdmin', body: `{"numeroPedido":"JMETER-${v('suffix')}","estado":"PENDING","subtotal":100,"descuento":0,"ivaTotal":0,"costoEnvio":0,"total":100,"notasCliente":"JMETER TEST","direccion":{"id":"${v('testAddressId')}"},"cuenta":{"id":"${v('testAccountId')}"}}`, expected: 201, extract: ['testOrderId', '$.id']});
add({label: 'ADMIN PUT pedido temporal', method: 'PUT', path: '/api/pedidos/${testOrderId}', token: 'jwtAdmin', body: `{"id":"${v('testOrderId')}","numeroPedido":"JMETER-${v('suffix')}","estado":"PENDING","subtotal":100,"descuento":0,"ivaTotal":0,"costoEnvio":5,"total":105,"notasCliente":"JMETER UPDATED","direccion":{"id":"${v('testAddressId')}"},"cuenta":{"id":"${v('testAccountId')}"}}`});
add({label: 'ADMIN POST item pedido temporal', method: 'POST', path: '/api/item-pedidos', token: 'jwtAdmin', body: `{"nombreProducto":"JMETER PRODUCTO","slugProducto":"jmeter","marcaProducto":"Test","skuProducto":"JMETER","cantidad":1,"precioUnitario":100,"porcentajeIva":0,"valorIva":0,"descuento":0,"subtotal":100,"pedido":{"id":"${v('testOrderId')}"},"producto":{"id":"${v('baseProductId')}"}}`, expected: 201, extract: ['testOrderItemId', '$.id']});
add({label: 'ADMIN PUT item pedido temporal', method: 'PUT', path: '/api/item-pedidos/${testOrderItemId}', token: 'jwtAdmin', body: `{"id":"${v('testOrderItemId')}","nombreProducto":"JMETER PRODUCTO UPDATED","cantidad":1,"precioUnitario":100,"pedido":{"id":"${v('testOrderId')}"},"producto":{"id":"${v('baseProductId')}"}}`});
add({label: 'ADMIN POST pago temporal', method: 'POST', path: '/api/pagos', token: 'jwtAdmin', body: `{"metodoPago":"CONTRA_ENTREGA","estado":"PENDING","monto":100,"pedido":{"id":"${v('testOrderId')}"}}`, expected: 201, extract: ['testPaymentId', '$.id']});
add({label: 'ADMIN PUT pago temporal', method: 'PUT', path: '/api/pagos/${testPaymentId}', token: 'jwtAdmin', body: `{"id":"${v('testPaymentId')}","metodoPago":"CONTRA_ENTREGA","estado":"PENDING","monto":105,"pedido":{"id":"${v('testOrderId')}"}}`});
add({label: 'ADMIN POST envio temporal', method: 'POST', path: '/api/envios', token: 'jwtAdmin', body: `{"transportadora":"JMETER","numeroRastreo":"JMETER-${v('suffix')}","tipoServicio":"ESTANDAR","estado":"PENDING","costoEnvio":5,"pesoKg":1,"valorDeclarado":100,"pedido":{"id":"${v('testOrderId')}"}}`, expected: 201, extract: ['testShipmentId', '$.id']});
add({label: 'ADMIN PUT envio temporal', method: 'PUT', path: '/api/envios/${testShipmentId}', token: 'jwtAdmin', body: `{"id":"${v('testShipmentId')}","transportadora":"JMETER UPDATED","numeroRastreo":"JMETER-UP-${v('suffix')}","tipoServicio":"EXPRESS","estado":"PENDING","costoEnvio":7,"pesoKg":1,"valorDeclarado":105,"pedido":{"id":"${v('testOrderId')}"}}`});
add({label: 'ADMIN POST factura temporal', method: 'POST', path: '/api/facturas', token: 'jwtAdmin', body: `{"prefijo":"JM","numero":"${v('suffix')}","subtotal":100,"descuentos":0,"baseGravableIva":100,"valorIva":0,"total":100,"enviada":false,"pago":{"id":"${v('testPaymentId')}"}}`, expected: 201, extract: ['testInvoiceId', '$.id']});
add({label: 'ADMIN PUT factura temporal', method: 'PUT', path: '/api/facturas/${testInvoiceId}', token: 'jwtAdmin', body: `{"id":"${v('testInvoiceId')}","prefijo":"JM","numero":"${v('suffix')}","subtotal":105,"descuentos":0,"baseGravableIva":105,"valorIva":0,"total":105,"enviada":false,"pago":{"id":"${v('testPaymentId')}"}}`});

add({label: 'ADMIN DELETE factura temporal', method: 'DELETE', path: '/api/facturas/${testInvoiceId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE envio temporal', method: 'DELETE', path: '/api/envios/${testShipmentId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE pago temporal', method: 'DELETE', path: '/api/pagos/${testPaymentId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE item pedido temporal', method: 'DELETE', path: '/api/item-pedidos/${testOrderItemId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE pedido temporal', method: 'DELETE', path: '/api/pedidos/${testOrderId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE item carrito temporal', method: 'DELETE', path: '/api/item-carritos/${testCartItemId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE carrito temporal', method: 'DELETE', path: '/api/carritos/${testCartId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE direccion temporal', method: 'DELETE', path: '/api/direccions/${testAddressId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE cuenta temporal', method: 'DELETE', path: '/api/cuentas/${testAccountId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE usuario temporal', method: 'DELETE', path: '/api/admin/users/jmeter_crud_${suffix}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE etiqueta temporal', method: 'DELETE', path: '/api/etiqueta-productos/${testLabelId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE imagen temporal', method: 'DELETE', path: '/api/producto-imagens/${testImageId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE producto temporal', method: 'DELETE', path: '/api/productos/${testProductId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE subcategoria temporal', method: 'DELETE', path: '/api/subcategorias/${testSubcategoryId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE categoria temporal', method: 'DELETE', path: '/api/categorias/${testCategoryId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE IVA temporal', method: 'DELETE', path: '/api/categoria-ivas/${testIvaId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE marca temporal', method: 'DELETE', path: '/api/marcas/${testBrandId}', token: 'jwtAdmin', expected: 204});
add({label: 'ADMIN DELETE tipo documento temporal', method: 'DELETE', path: '/api/tipo-documentos/${testDocumentTypeId}', token: 'jwtAdmin', expected: 204});

add({label: 'MANAGER POST autoridad - 403 esperado', method: 'POST', path: '/api/authorities', token: 'jwtManager', body: `{"name":"ROLE_JMETER_FORBIDDEN_${v('suffix')}"}`, expected: 403});
add({label: 'CLIENTE A GET cuenta propia', method: 'GET', path: '/api/cuentas/${accountAId}', token: 'jwtClientA'});
add({label: 'CLIENTE B GET cuenta ajena - 403 esperado', method: 'GET', path: '/api/cuentas/${accountAId}', token: 'jwtClientB', expected: 403});
add({label: 'USER GET cuentas protegidas - 403 esperado', method: 'GET', path: '/api/cuentas?page=0&size=1', token: 'jwtUser', expected: 403});

const vars = `<elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="Variables de produccion" enabled="true"><collectionProp name="Arguments.arguments"><elementProp name="protocol" elementType="Argument"><stringProp name="Argument.name">protocol</stringProp><stringProp name="Argument.value">${p('protocol', 'https')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="host" elementType="Argument"><stringProp name="Argument.name">host</stringProp><stringProp name="Argument.value">${p('host', 'app.knstore.duckdns.org')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="port" elementType="Argument"><stringProp name="Argument.name">port</stringProp><stringProp name="Argument.value">${p('port', '')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="password" elementType="Argument"><stringProp name="Argument.name">password</stringProp><stringProp name="Argument.value">${p('password', '123456')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="adminUser" elementType="Argument"><stringProp name="Argument.name">adminUser</stringProp><stringProp name="Argument.value">${p('adminUser', 'jmeter_admin')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="managerUser" elementType="Argument"><stringProp name="Argument.name">managerUser</stringProp><stringProp name="Argument.value">${p('managerUser', 'jmeter_manager')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="clientAUser" elementType="Argument"><stringProp name="Argument.name">clientAUser</stringProp><stringProp name="Argument.value">${p('clientAUser', 'jmeter_cliente_a')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="clientBUser" elementType="Argument"><stringProp name="Argument.name">clientBUser</stringProp><stringProp name="Argument.value">${p('clientBUser', 'jmeter_cliente_b')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="userUser" elementType="Argument"><stringProp name="Argument.name">userUser</stringProp><stringProp name="Argument.value">${p('userUser', 'jmeter_user')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="suffix" elementType="Argument"><stringProp name="Argument.name">suffix</stringProp><stringProp name="Argument.value">${p('suffix', '${__time(yyyyMMddHHmmss)}')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="testDocNumber" elementType="Argument"><stringProp name="Argument.name">testDocNumber</stringProp><stringProp name="Argument.value">${p('testDocNumber', '${__Random(1000000000,9999999999)}')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp><elementProp name="accountAId" elementType="Argument"><stringProp name="Argument.name">accountAId</stringProp><stringProp name="Argument.value">${p('accountAId', '6a8500181a69e7ed4125ac70')}</stringProp><stringProp name="Argument.metadata">=</stringProp></elementProp></collectionProp></elementProp>`;

const plan = `<?xml version="1.0" encoding="UTF-8"?><jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3"><hashTree><TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="KN-Store - Produccion - CRUD por roles" enabled="true"><stringProp name="TestPlan.comments">Ciclo temporal POST PUT DELETE en produccion. Requiere password=123456 y limpia los registros creados con prefijo JMETER.</stringProp><boolProp name="TestPlan.functional_mode">false</boolProp><boolProp name="TestPlan.serialize_threadgroups">false</boolProp>${vars}<stringProp name="TestPlan.user_define_classpath"></stringProp></TestPlan><hashTree><ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Produccion - CRUD lifecycle y permisos" enabled="true"><stringProp name="ThreadGroup.on_sample_error">continue</stringProp><elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Una ejecucion secuencial" enabled="true"><boolProp name="LoopController.continue_forever">false</boolProp><stringProp name="LoopController.loops">1</stringProp></elementProp><stringProp name="ThreadGroup.num_threads">1</stringProp><stringProp name="ThreadGroup.ramp_time">1</stringProp><longProp name="ThreadGroup.start_time">0</longProp><longProp name="ThreadGroup.end_time">0</longProp><boolProp name="ThreadGroup.scheduler">false</boolProp><stringProp name="ThreadGroup.duration"></stringProp><stringProp name="ThreadGroup.delay"></stringProp><boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp></ThreadGroup><hashTree><ConfigTestElement guiclass="HttpDefaultsGui" testclass="ConfigTestElement" testname="HTTP Request Defaults" enabled="true"><stringProp name="HTTPSampler.domain">${v('host')}</stringProp><stringProp name="HTTPSampler.port">${v('port')}</stringProp><stringProp name="HTTPSampler.protocol">${v('protocol')}</stringProp><stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp><elementProp name="HTTPsampler.Arguments" elementType="Arguments"><collectionProp name="Arguments.arguments"/></elementProp><stringProp name="HTTPSampler.connect_timeout">15000</stringProp><stringProp name="HTTPSampler.response_timeout">30000</stringProp></ConfigTestElement><hashTree/><CookieManager guiclass="CookiePanel" testclass="CookieManager" testname="Cookie Manager" enabled="true"><collectionProp name="CookieManager.cookies"/><boolProp name="CookieManager.clearEachIteration">false</boolProp><boolProp name="CookieManager.controlledByThreadGroup">false</boolProp></CookieManager><hashTree/>${steps.join('')} ${listeners()}</hashTree></hashTree></hashTree></jmeterTestPlan>\n`;

fs.writeFileSync(outputPath, plan, 'utf8');
console.log(`Generated ${steps.length} production lifecycle samples at ${outputPath}`);
