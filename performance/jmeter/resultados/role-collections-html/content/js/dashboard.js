/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9705882352941176, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "037 GET /api/cuentas :: getAllCuentas"], "isController": false}, {"data": [0.5, 500, 1500, "00.01 POST /api/authenticate"], "isController": false}, {"data": [1.0, 500, 1500, "038 GET /api/carritos :: getAllCarritos"], "isController": false}, {"data": [1.0, 500, 1500, "036 GET /api/direccions :: getAllDireccions"], "isController": false}, {"data": [1.0, 500, 1500, "022 GET /api/tipo-documentos :: getAllTipoDocumentos"], "isController": false}, {"data": [1.0, 500, 1500, "034 GET /api/direccions :: getAllDireccions"], "isController": false}, {"data": [1.0, 500, 1500, "023 GET /api/subcategorias :: getAllSubcategorias"], "isController": false}, {"data": [1.0, 500, 1500, "026 GET /api/producto-inventarios :: getAllProductoInventarios"], "isController": false}, {"data": [1.0, 500, 1500, "027 GET /api/pagos :: getAllPagos"], "isController": false}, {"data": [1.0, 500, 1500, "033 GET /api/envios :: getAllEnvios"], "isController": false}, {"data": [1.0, 500, 1500, "039 GET /api/categoria-ivas :: getAllCategoriaIVAS"], "isController": false}, {"data": [1.0, 500, 1500, "029 GET /api/item-pedidos :: getAllItemPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "023 GET /api/producto-precios :: getAllProductoPrecios"], "isController": false}, {"data": [1.0, 500, 1500, "039 GET /api/authenticate :: isAuthenticated"], "isController": false}, {"data": [1.0, 500, 1500, "040 GET /api/carritos :: getAllCarritos"], "isController": false}, {"data": [1.0, 500, 1500, "035 GET /api/cuentas :: getAllCuentas"], "isController": false}, {"data": [1.0, 500, 1500, "041 GET /api/authorities :: getAllAuthorities"], "isController": false}, {"data": [1.0, 500, 1500, "032 GET /api/etiqueta-productos :: getAllEtiquetaProductos"], "isController": false}, {"data": [1.0, 500, 1500, "025 GET /api/producto-precios :: getAllProductoPrecios"], "isController": false}, {"data": [1.0, 500, 1500, "029 GET /api/pagos :: getAllPagos"], "isController": false}, {"data": [1.0, 500, 1500, "035 GET /api/envios :: getAllEnvios"], "isController": false}, {"data": [1.0, 500, 1500, "040 GET /api/account :: getAccount"], "isController": false}, {"data": [1.0, 500, 1500, "024 GET /api/producto-inventarios :: getAllProductoInventarios"], "isController": false}, {"data": [1.0, 500, 1500, "031 GET /api/facturas :: getAllFacturas"], "isController": false}, {"data": [1.0, 500, 1500, "037 GET /api/categoria-ivas :: getAllCategoriaIVAS"], "isController": false}, {"data": [1.0, 500, 1500, "028 GET /api/pedidos :: getAllPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "034 GET /api/etiqueta-productos :: getAllEtiquetaProductos"], "isController": false}, {"data": [1.0, 500, 1500, "022 GET /api/productos :: getAllProductos"], "isController": false}, {"data": [1.0, 500, 1500, "024 GET /api/productos :: getAllProductos"], "isController": false}, {"data": [1.0, 500, 1500, "042 GET /api/authenticate :: isAuthenticated"], "isController": false}, {"data": [1.0, 500, 1500, "021 GET /api/admin/users :: getAllUsers"], "isController": false}, {"data": [1.0, 500, 1500, "028 GET /api/marcas :: getAllMarcas"], "isController": false}, {"data": [1.0, 500, 1500, "043 GET /api/account :: getAccount"], "isController": false}, {"data": [1.0, 500, 1500, "031 GET /api/item-pedidos :: getAllItemPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "032 GET /api/item-carritos :: getAllItemCarritos"], "isController": false}, {"data": [1.0, 500, 1500, "026 GET /api/pedidos :: getAllPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "041 GET /api/users :: getAllPublicUsers"], "isController": false}, {"data": [1.0, 500, 1500, "021 GET /api/subcategorias :: getAllSubcategorias"], "isController": false}, {"data": [1.0, 500, 1500, "033 GET /api/facturas :: getAllFacturas"], "isController": false}, {"data": [1.0, 500, 1500, "025 GET /api/producto-imagens :: getAllProductoImagens"], "isController": false}, {"data": [1.0, 500, 1500, "030 GET /api/marcas :: getAllMarcas"], "isController": false}, {"data": [1.0, 500, 1500, "038 GET /api/categorias :: getAllCategorias"], "isController": false}, {"data": [1.0, 500, 1500, "036 GET /api/categorias :: getAllCategorias"], "isController": false}, {"data": [1.0, 500, 1500, "020 GET /api/tipo-documentos :: getAllTipoDocumentos"], "isController": false}, {"data": [1.0, 500, 1500, "044 GET /api/users :: getAllPublicUsers"], "isController": false}, {"data": [1.0, 500, 1500, "030 GET /api/item-carritos :: getAllItemCarritos"], "isController": false}, {"data": [0.5, 500, 1500, "027 GET /api/producto-imagens :: getAllProductoImagens"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 119, 0, 0.0, 139.33613445378145, 71, 920, 89.0, 200.0, 780.0, 904.5999999999998, 29.252704031465093, 91.3688487125123, 13.65603106563422], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["037 GET /api/cuentas :: getAllCuentas", 2, 0, 0.0, 83.0, 80, 86, 83.0, 86.0, 86.0, 86.0, 20.408163265306122, 53.21269132653061, 9.954958545918368], "isController": false}, {"data": ["00.01 POST /api/authenticate", 5, 0, 0.0, 780.0, 780, 780, 780.0, 780.0, 780.0, 780.0, 6.41025641025641, 9.735576923076923, 1.8141526442307692], "isController": false}, {"data": ["038 GET /api/carritos :: getAllCarritos", 3, 0, 0.0, 78.66666666666667, 78, 80, 78.0, 80.0, 80.0, 80.0, 25.210084033613445, 24.487920168067227, 11.6530987394958], "isController": false}, {"data": ["036 GET /api/direccions :: getAllDireccions", 2, 0, 0.0, 78.0, 76, 80, 78.0, 80.0, 80.0, 80.0, 22.47191011235955, 37.723841292134836, 10.698297050561798], "isController": false}, {"data": ["022 GET /api/tipo-documentos :: getAllTipoDocumentos", 2, 0, 0.0, 101.5, 99, 104, 101.5, 104.0, 104.0, 104.0, 9.00900900900901, 10.75098536036036, 4.2097761824324325], "isController": false}, {"data": ["034 GET /api/direccions :: getAllDireccions", 3, 0, 0.0, 93.0, 86, 99, 94.0, 99.0, 99.0, 99.0, 22.727272727272727, 24.976325757575758, 10.860558712121211], "isController": false}, {"data": ["023 GET /api/subcategorias :: getAllSubcategorias", 2, 0, 0.0, 120.0, 108, 132, 120.0, 132.0, 132.0, 132.0, 7.968127490039841, 17.539218127490038, 3.933484810756972], "isController": false}, {"data": ["026 GET /api/producto-inventarios :: getAllProductoInventarios", 2, 0, 0.0, 89.0, 88, 90, 89.0, 90.0, 90.0, 90.0, 9.66183574879227, 143.455615942029, 4.5620093599033815], "isController": false}, {"data": ["027 GET /api/pagos :: getAllPagos", 3, 0, 0.0, 90.33333333333333, 88, 92, 91.0, 92.0, 92.0, 92.0, 32.608695652173914, 35.517153532608695, 15.423318614130435], "isController": false}, {"data": ["033 GET /api/envios :: getAllEnvios", 3, 0, 0.0, 102.66666666666667, 92, 111, 105.0, 111.0, 111.0, 111.0, 22.55639097744361, 24.612312030075188, 10.69078947368421], "isController": false}, {"data": ["039 GET /api/categoria-ivas :: getAllCategoriaIVAS", 2, 0, 0.0, 73.0, 72, 74, 73.0, 74.0, 74.0, 74.0, 18.51851851851852, 18.428096064814817, 8.635344328703704], "isController": false}, {"data": ["029 GET /api/item-pedidos :: getAllItemPedidos", 3, 0, 0.0, 98.0, 88, 105, 101.0, 105.0, 105.0, 105.0, 25.0, 24.348958333333336, 12.019856770833334], "isController": false}, {"data": ["023 GET /api/producto-precios :: getAllProductoPrecios", 3, 0, 0.0, 81.66666666666667, 77, 87, 81.0, 87.0, 87.0, 87.0, 25.0, 27.734375, 11.751302083333334], "isController": false}, {"data": ["039 GET /api/authenticate :: isAuthenticated", 3, 0, 0.0, 79.66666666666667, 71, 90, 78.0, 90.0, 90.0, 90.0, 24.793388429752067, 21.282605888429753, 11.557334710743802], "isController": false}, {"data": ["040 GET /api/carritos :: getAllCarritos", 2, 0, 0.0, 76.0, 75, 77, 76.0, 77.0, 77.0, 77.0, 17.391304347826086, 23.369565217391305, 8.0078125], "isController": false}, {"data": ["035 GET /api/cuentas :: getAllCuentas", 3, 0, 0.0, 77.0, 76, 78, 77.0, 78.0, 78.0, 78.0, 24.0, 34.953125, 11.75], "isController": false}, {"data": ["041 GET /api/authorities :: getAllAuthorities", 2, 0, 0.0, 74.5, 74, 75, 74.5, 75.0, 75.0, 75.0, 17.391304347826086, 18.248980978260867, 8.058763586956522], "isController": false}, {"data": ["032 GET /api/etiqueta-productos :: getAllEtiquetaProductos", 3, 0, 0.0, 89.33333333333333, 87, 92, 89.0, 92.0, 92.0, 92.0, 27.027027027027028, 30.08868243243243, 13.522311373873874], "isController": false}, {"data": ["025 GET /api/producto-precios :: getAllProductoPrecios", 2, 0, 0.0, 196.0, 174, 218, 196.0, 218.0, 218.0, 218.0, 6.024096385542169, 88.8671875, 2.8208537274096384], "isController": false}, {"data": ["029 GET /api/pagos :: getAllPagos", 2, 0, 0.0, 86.0, 86, 86, 86.0, 86.0, 86.0, 86.0, 16.949152542372882, 28.43617584745763, 7.986295021186441], "isController": false}, {"data": ["035 GET /api/envios :: getAllEnvios", 2, 0, 0.0, 83.0, 83, 83, 83.0, 83.0, 83.0, 83.0, 21.978021978021978, 37.238152472527474, 10.377317994505495], "isController": false}, {"data": ["040 GET /api/account :: getAccount", 3, 0, 0.0, 100.0, 75, 113, 112.0, 113.0, 113.0, 113.0, 18.29268292682927, 22.81226181402439, 8.43773818597561], "isController": false}, {"data": ["024 GET /api/producto-inventarios :: getAllProductoInventarios", 3, 0, 0.0, 125.0, 108, 134, 133.0, 134.0, 134.0, 134.0, 20.408163265306122, 22.799744897959187, 9.672619047619047], "isController": false}, {"data": ["031 GET /api/facturas :: getAllFacturas", 3, 0, 0.0, 95.66666666666667, 94, 98, 95.0, 98.0, 98.0, 98.0, 26.08695652173913, 28.56657608695652, 12.415081521739129], "isController": false}, {"data": ["037 GET /api/categoria-ivas :: getAllCategoriaIVAS", 3, 0, 0.0, 75.0, 74, 76, 75.0, 76.0, 76.0, 76.0, 24.793388429752067, 27.40831611570248, 11.605759297520661], "isController": false}, {"data": ["028 GET /api/pedidos :: getAllPedidos", 2, 0, 0.0, 85.5, 83, 88, 85.5, 88.0, 88.0, 88.0, 16.129032258064516, 31.17124495967742, 7.631363407258065], "isController": false}, {"data": ["034 GET /api/etiqueta-productos :: getAllEtiquetaProductos", 2, 0, 0.0, 79.0, 77, 81, 79.0, 81.0, 81.0, 81.0, 23.52941176470588, 26.93014705882353, 11.730238970588234], "isController": false}, {"data": ["022 GET /api/productos :: getAllProductos", 3, 0, 0.0, 244.33333333333334, 229, 268, 236.0, 268.0, 268.0, 268.0, 11.194029850746269, 74.67423624067163, 5.502273787313433], "isController": false}, {"data": ["024 GET /api/productos :: getAllProductos", 2, 0, 0.0, 193.0, 186, 200, 193.0, 200.0, 200.0, 200.0, 5.797101449275362, 38.671875, 2.8391077898550727], "isController": false}, {"data": ["042 GET /api/authenticate :: isAuthenticated", 2, 0, 0.0, 77.0, 76, 78, 77.0, 78.0, 78.0, 78.0, 16.80672268907563, 14.42686449579832, 7.804293592436975], "isController": false}, {"data": ["021 GET /api/admin/users :: getAllUsers", 2, 0, 0.0, 102.0, 102, 102, 102.0, 102.0, 102.0, 102.0, 19.607843137254903, 31.508501838235297, 9.353936887254903], "isController": false}, {"data": ["028 GET /api/marcas :: getAllMarcas", 3, 0, 0.0, 92.66666666666667, 81, 99, 98.0, 99.0, 99.0, 99.0, 30.303030303030305, 33.41027462121212, 13.948074494949495], "isController": false}, {"data": ["043 GET /api/account :: getAccount", 2, 0, 0.0, 82.5, 77, 88, 82.5, 88.0, 88.0, 88.0, 16.666666666666668, 20.751953125, 7.657877604166667], "isController": false}, {"data": ["031 GET /api/item-pedidos :: getAllItemPedidos", 2, 0, 0.0, 124.5, 123, 126, 124.5, 126.0, 126.0, 126.0, 15.873015873015872, 29.8859126984127, 7.603236607142857], "isController": false}, {"data": ["032 GET /api/item-carritos :: getAllItemCarritos", 2, 0, 0.0, 89.0, 87, 91, 89.0, 91.0, 91.0, 91.0, 21.978021978021978, 32.6665521978022, 10.549021291208792], "isController": false}, {"data": ["026 GET /api/pedidos :: getAllPedidos", 3, 0, 0.0, 85.0, 79, 89, 87.0, 89.0, 89.0, 89.0, 32.96703296703297, 36.036229395604394, 15.657194368131869], "isController": false}, {"data": ["041 GET /api/users :: getAllPublicUsers", 3, 0, 0.0, 84.66666666666667, 79, 96, 79.0, 96.0, 96.0, 96.0, 17.75147928994083, 22.2240199704142, 8.396126109467454], "isController": false}, {"data": ["021 GET /api/subcategorias :: getAllSubcategorias", 3, 0, 0.0, 100.66666666666667, 96, 103, 103.0, 103.0, 103.0, 103.0, 29.12621359223301, 64.11180218446603, 14.430370145631068], "isController": false}, {"data": ["033 GET /api/facturas :: getAllFacturas", 2, 0, 0.0, 91.5, 91, 92, 91.5, 92.0, 92.0, 92.0, 21.052631578947366, 35.79358552631579, 9.981496710526315], "isController": false}, {"data": ["025 GET /api/producto-imagens :: getAllProductoImagens", 3, 0, 0.0, 85.66666666666667, 84, 87, 86.0, 87.0, 87.0, 87.0, 30.303030303030305, 33.61742424242424, 14.244002525252524], "isController": false}, {"data": ["030 GET /api/marcas :: getAllMarcas", 2, 0, 0.0, 94.5, 80, 109, 94.5, 109.0, 109.0, 109.0, 17.857142857142858, 19.688197544642858, 8.187430245535714], "isController": false}, {"data": ["038 GET /api/categorias :: getAllCategorias", 2, 0, 0.0, 84.0, 76, 92, 84.0, 92.0, 92.0, 92.0, 18.18181818181818, 29.279119318181817, 8.655894886363637], "isController": false}, {"data": ["036 GET /api/categorias :: getAllCategorias", 3, 0, 0.0, 76.0, 74, 77, 77.0, 77.0, 77.0, 77.0, 24.793388429752067, 39.92607179752066, 11.84788223140496], "isController": false}, {"data": ["020 GET /api/tipo-documentos :: getAllTipoDocumentos", 3, 0, 0.0, 95.66666666666667, 95, 97, 95.0, 97.0, 97.0, 97.0, 30.927835051546392, 36.908021907216494, 14.507490335051546], "isController": false}, {"data": ["044 GET /api/users :: getAllPublicUsers", 2, 0, 0.0, 79.0, 77, 81, 79.0, 81.0, 81.0, 81.0, 17.699115044247787, 22.15846238938053, 8.339670907079645], "isController": false}, {"data": ["030 GET /api/item-carritos :: getAllItemCarritos", 3, 0, 0.0, 94.33333333333333, 89, 97, 97.0, 97.0, 97.0, 97.0, 27.027027027027028, 26.34079391891892, 13.020833333333334], "isController": false}, {"data": ["027 GET /api/producto-imagens :: getAllProductoImagens", 2, 0, 0.0, 881.5, 843, 920, 881.5, 920.0, 920.0, 920.0, 2.0833333333333335, 142.11018880208334, 0.9755452473958334], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 119, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
