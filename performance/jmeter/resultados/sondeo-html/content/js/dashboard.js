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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.3092105263157895, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "001 GET /api/categoria-ivas :: getAllCategoriaIVAS"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/marcas :: getAllMarcas"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/item-pedidos :: getAllItemPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/cuentas :: getAllCuentas"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/authenticate :: isAuthenticated"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/item-carritos :: getAllItemCarritos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/tipo-documentos :: getAllTipoDocumentos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/productos :: getAllProductos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/producto-inventarios :: getAllProductoInventarios"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/producto-precios :: getAllProductoPrecios"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/envios :: getAllEnvios"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/account :: getAccount"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/pedidos :: getAllPedidos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/categorias :: getAllCategorias"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/carritos :: getAllCarritos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/facturas :: getAllFacturas"], "isController": false}, {"data": [0.5, 500, 1500, "001 GET /api/producto-imagens :: getAllProductoImagens"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/direccions :: getAllDireccions"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/pagos :: getAllPagos"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/admin/users :: getAllUsers"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/users :: getAllPublicUsers"], "isController": false}, {"data": [0.0, 500, 1500, "00.01 POST /api/authenticate :: authorize"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/authorities :: getAllAuthorities"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/subcategorias :: getAllSubcategorias"], "isController": false}, {"data": [1.0, 500, 1500, "001 GET /api/etiqueta-productos :: getAllEtiquetaProductos"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 76, 0, 0.0, 2219.565789473684, 103, 3376, 3061.0, 3339.9, 3357.0, 3376.0, 18.51400730816078, 51.15760124847746, 6.229303060292326], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["001 GET /api/categoria-ivas :: getAllCategoriaIVAS", 1, 0, 0.0, 107.0, 107, 107, 107.0, 107.0, 107.0, 107.0, 9.345794392523365, 9.300160630841122, 4.1344188084112155], "isController": false}, {"data": ["001 GET /api/marcas :: getAllMarcas", 1, 0, 0.0, 230.0, 230, 230, 230.0, 230.0, 230.0, 230.0, 4.3478260869565215, 4.793648097826087, 1.8894361413043477], "isController": false}, {"data": ["001 GET /api/item-pedidos :: getAllItemPedidos", 1, 0, 0.0, 424.0, 424, 424, 424.0, 424.0, 424.0, 424.0, 2.3584905660377355, 4.440595518867925, 1.0732974646226416], "isController": false}, {"data": ["001 GET /api/cuentas :: getAllCuentas", 1, 0, 0.0, 117.0, 117, 117, 117.0, 117.0, 117.0, 117.0, 8.547008547008549, 13.855502136752136, 3.964676816239316], "isController": false}, {"data": ["001 GET /api/authenticate :: isAuthenticated", 1, 0, 0.0, 233.0, 233, 233, 233.0, 233.0, 233.0, 233.0, 4.291845493562231, 3.6841134656652357, 1.890256169527897], "isController": false}, {"data": ["001 GET /api/item-carritos :: getAllItemCarritos", 1, 0, 0.0, 134.0, 134, 134, 134.0, 134.0, 134.0, 134.0, 7.462686567164179, 11.09200093283582, 3.403393190298507], "isController": false}, {"data": ["001 GET /api/tipo-documentos :: getAllTipoDocumentos", 1, 0, 0.0, 121.0, 121, 121, 121.0, 121.0, 121.0, 121.0, 8.264462809917356, 9.047326962809917, 3.6641270661157024], "isController": false}, {"data": ["001 GET /api/productos :: getAllProductos", 1, 0, 0.0, 227.0, 227, 227, 227.0, 227.0, 227.0, 227.0, 4.405286343612335, 29.387217786343612, 2.0520718612334803], "isController": false}, {"data": ["001 GET /api/producto-inventarios :: getAllProductoInventarios", 1, 0, 0.0, 196.0, 196, 196, 196.0, 196.0, 196.0, 196.0, 5.1020408163265305, 74.01945153061224, 2.28694993622449], "isController": false}, {"data": ["001 GET /api/producto-precios :: getAllProductoPrecios", 1, 0, 0.0, 203.0, 203, 203, 203.0, 203.0, 203.0, 203.0, 4.926108374384237, 71.01004464285714, 2.1888469827586206], "isController": false}, {"data": ["001 GET /api/envios :: getAllEnvios", 1, 0, 0.0, 202.0, 202, 202, 202.0, 202.0, 202.0, 202.0, 4.9504950495049505, 8.38780167079208, 2.21902073019802], "isController": false}, {"data": ["001 GET /api/account :: getAccount", 1, 0, 0.0, 150.0, 150, 150, 150.0, 150.0, 150.0, 150.0, 6.666666666666667, 8.196614583333334, 2.9036458333333335], "isController": false}, {"data": ["001 GET /api/pedidos :: getAllPedidos", 1, 0, 0.0, 222.0, 222, 222, 222.0, 222.0, 222.0, 222.0, 4.504504504504505, 8.705482826576576, 2.0235078828828827], "isController": false}, {"data": ["001 GET /api/categorias :: getAllCategorias", 1, 0, 0.0, 116.0, 116, 116, 116.0, 116.0, 116.0, 116.0, 8.620689655172413, 13.882341056034482, 3.8978313577586206], "isController": false}, {"data": ["001 GET /api/carritos :: getAllCarritos", 1, 0, 0.0, 226.0, 226, 226, 226.0, 226.0, 226.0, 226.0, 4.424778761061947, 5.945796460176991, 1.9315196349557522], "isController": false}, {"data": ["001 GET /api/facturas :: getAllFacturas", 1, 0, 0.0, 103.0, 103, 103, 103.0, 103.0, 103.0, 103.0, 9.70873786407767, 16.506750606796118, 4.370828276699029], "isController": false}, {"data": ["001 GET /api/producto-imagens :: getAllProductoImagens", 1, 0, 0.0, 771.0, 771, 771, 771.0, 771.0, 771.0, 771.0, 1.297016861219196, 88.47326929312581, 0.5763112029831388], "isController": false}, {"data": ["001 GET /api/direccions :: getAllDireccions", 1, 0, 0.0, 333.0, 333, 333, 333.0, 333.0, 333.0, 333.0, 3.003003003003003, 5.041173986486486, 1.3578031156156156], "isController": false}, {"data": ["001 GET /api/pagos :: getAllPagos", 1, 0, 0.0, 106.0, 106, 106, 106.0, 106.0, 106.0, 106.0, 9.433962264150942, 15.827682783018869, 4.219487028301887], "isController": false}, {"data": ["001 GET /api/admin/users :: getAllUsers", 1, 0, 0.0, 114.0, 114, 114, 114.0, 114.0, 114.0, 114.0, 8.771929824561402, 18.537554824561404, 3.9747807017543857], "isController": false}, {"data": ["001 GET /api/users :: getAllPublicUsers", 1, 0, 0.0, 148.0, 148, 148, 148.0, 148.0, 148.0, 148.0, 6.756756756756757, 8.452544341216216, 3.0220650337837838], "isController": false}, {"data": ["00.01 POST /api/authenticate :: authorize", 52, 0, 0.0, 3145.8653846153848, 2637, 3376, 3184.0, 3355.7, 3362.25, 3376.0, 15.402843601895734, 22.683093898104268, 4.377175281398104], "isController": false}, {"data": ["001 GET /api/authorities :: getAllAuthorities", 1, 0, 0.0, 137.0, 137, 137, 137.0, 137.0, 137.0, 137.0, 7.299270072992701, 7.292141879562044, 3.20768704379562], "isController": false}, {"data": ["001 GET /api/subcategorias :: getAllSubcategorias", 1, 0, 0.0, 119.0, 119, 119, 119.0, 119.0, 119.0, 119.0, 8.403361344537815, 18.497242647058826, 3.947282037815126], "isController": false}, {"data": ["001 GET /api/etiqueta-productos :: getAllEtiquetaProductos", 1, 0, 0.0, 363.0, 363, 363, 363.0, 363.0, 363.0, 363.0, 2.7548209366391188, 3.152978650137741, 1.3074638429752066], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 76, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
