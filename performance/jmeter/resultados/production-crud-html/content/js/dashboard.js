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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9863013698630136, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "ADMIN GET reference subcategoria"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST etiqueta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE tipo documento temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST inventario temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE subcategoria temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE IVA temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT item pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT inventario temporal"], "isController": false}, {"data": [0.5, 500, 1500, "ADMIN POST imagen temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST pago temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST item pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE item carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT etiqueta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST envio temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT direccion temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE cuenta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE marca temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST categoria IVA temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST producto temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST subcategoria temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT categoria IVA temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "CLIENTE A GET cuenta propia"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN GET reference IVA"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN GET reference tipo documento"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE item pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN GET reference categoria"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST factura temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST categoria temporal"], "isController": false}, {"data": [0.5, 500, 1500, "AUTH adminUser"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE usuario temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST cuenta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "AUTH managerUser"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "AUTH userUser"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT usuario temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT categoria temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST tipo documento temporal"], "isController": false}, {"data": [1.0, 500, 1500, "MANAGER POST autoridad - 403 esperado"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT pedido temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN GET reference marca"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT tipo documento temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE etiqueta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE categoria temporal"], "isController": false}, {"data": [1.0, 500, 1500, "CLIENTE B GET cuenta ajena - 403 esperado"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT precio temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE producto temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT pago temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST usuario temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT imagen temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT envio temporal"], "isController": false}, {"data": [1.0, 500, 1500, "AUTH clientAUser"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE direccion temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT marca temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT factura temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST marca temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE pago temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST item carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "USER GET cuentas protegidas - 403 esperado"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN GET reference producto"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT cuenta temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT producto temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE imagen temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE envio temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT subcategoria temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST precio temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN POST direccion temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN PUT item carrito temporal"], "isController": false}, {"data": [1.0, 500, 1500, "ADMIN DELETE factura temporal"], "isController": false}, {"data": [1.0, 500, 1500, "AUTH clientBUser"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 73, 0, 0.0, 143.15068493150685, 79, 1403, 94.0, 226.40000000000003, 328.89999999999947, 1403.0, 6.673370509187312, 9.107500971295366, 4.071048827589359], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["ADMIN GET reference subcategoria", 1, 0, 0.0, 87.0, 87, 87, 87.0, 87.0, 87.0, 87.0, 11.494252873563218, 17.903645833333336, 5.62365301724138], "isController": false}, {"data": ["ADMIN POST etiqueta temporal", 1, 0, 0.0, 135.0, 135, 135, 135.0, 135.0, 135.0, 135.0, 7.407407407407407, 11.234085648148147, 4.4126157407407405], "isController": false}, {"data": ["ADMIN DELETE tipo documento temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 11.01345486111111, 6.34765625], "isController": false}, {"data": ["ADMIN POST inventario temporal", 1, 0, 0.0, 258.0, 258, 258, 258.0, 258.0, 258.0, 258.0, 3.875968992248062, 4.814680232558139, 2.3278524709302326], "isController": false}, {"data": ["ADMIN DELETE subcategoria temporal", 1, 0, 0.0, 99.0, 99, 99, 99.0, 99.0, 99.0, 99.0, 10.101010101010102, 10.002367424242424, 5.750868055555555], "isController": false}, {"data": ["ADMIN DELETE IVA temporal", 1, 0, 0.0, 83.0, 83, 83, 83.0, 83.0, 83.0, 83.0, 12.048192771084338, 11.930534638554215, 6.871234939759036], "isController": false}, {"data": ["ADMIN POST pedido temporal", 1, 0, 0.0, 85.0, 85, 85, 85.0, 85.0, 85.0, 85.0, 11.76470588235294, 22.74816176470588, 8.731617647058822], "isController": false}, {"data": ["ADMIN PUT item pedido temporal", 1, 0, 0.0, 84.0, 84, 84, 84.0, 84.0, 84.0, 84.0, 11.904761904761903, 23.007347470238095, 8.684430803571429], "isController": false}, {"data": ["ADMIN PUT inventario temporal", 1, 0, 0.0, 224.0, 224, 224, 224.0, 224.0, 224.0, 224.0, 4.464285714285714, 5.257742745535714, 2.9296875], "isController": false}, {"data": ["ADMIN POST imagen temporal", 1, 0, 0.0, 514.0, 514, 514, 514.0, 514.0, 514.0, 514.0, 1.9455252918287937, 3.1576787451361867, 1.3451483463035019], "isController": false}, {"data": ["ADMIN POST pago temporal", 1, 0, 0.0, 97.0, 97, 97, 97.0, 97.0, 97.0, 97.0, 10.309278350515465, 16.732442010309278, 6.272148840206185], "isController": false}, {"data": ["ADMIN POST item pedido temporal", 1, 0, 0.0, 125.0, 125, 125, 125.0, 125.0, 125.0, 125.0, 8.0, 15.8515625, 6.3515625], "isController": false}, {"data": ["ADMIN DELETE item carrito temporal", 1, 0, 0.0, 93.0, 93, 93, 93.0, 93.0, 93.0, 93.0, 10.752688172043012, 10.637180779569892, 6.121891801075269], "isController": false}, {"data": ["ADMIN PUT etiqueta temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 16.18923611111111, 7.269965277777778], "isController": false}, {"data": ["ADMIN POST envio temporal", 1, 0, 0.0, 97.0, 97, 97, 97.0, 97.0, 97.0, 97.0, 10.309278350515465, 17.165351159793815, 7.198373067010309], "isController": false}, {"data": ["ADMIN PUT direccion temporal", 1, 0, 0.0, 84.0, 84, 84, 84.0, 84.0, 84.0, 84.0, 11.904761904761903, 19.717261904761905, 10.033017113095237], "isController": false}, {"data": ["ADMIN DELETE cuenta temporal", 1, 0, 0.0, 79.0, 79, 79, 79.0, 79.0, 79.0, 79.0, 12.658227848101266, 12.460443037974683, 7.132614715189874], "isController": false}, {"data": ["ADMIN DELETE marca temporal", 1, 0, 0.0, 85.0, 85, 85, 85.0, 85.0, 85.0, 85.0, 11.76470588235294, 11.56939338235294, 6.617647058823529], "isController": false}, {"data": ["ADMIN POST categoria IVA temporal", 1, 0, 0.0, 104.0, 104, 104, 104.0, 104.0, 104.0, 104.0, 9.615384615384617, 11.662409855769232, 5.549504206730769], "isController": false}, {"data": ["ADMIN POST producto temporal", 1, 0, 0.0, 92.0, 92, 92, 92.0, 92.0, 92.0, 92.0, 10.869565217391305, 20.67764945652174, 10.084069293478262], "isController": false}, {"data": ["ADMIN POST subcategoria temporal", 1, 0, 0.0, 82.0, 82, 82, 82.0, 82.0, 82.0, 82.0, 12.195121951219512, 17.828220274390244, 8.443692835365853], "isController": false}, {"data": ["ADMIN PUT categoria IVA temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 12.923177083333334, 7.12890625], "isController": false}, {"data": ["ADMIN PUT carrito temporal", 1, 0, 0.0, 82.0, 82, 82, 82.0, 82.0, 82.0, 82.0, 12.195121951219512, 17.79249237804878, 7.550495426829268], "isController": false}, {"data": ["CLIENTE A GET cuenta propia", 1, 0, 0.0, 98.0, 98, 98, 98.0, 98.0, 98.0, 98.0, 10.204081632653061, 14.728156887755102, 4.97249681122449], "isController": false}, {"data": ["ADMIN GET reference IVA", 1, 0, 0.0, 85.0, 85, 85, 85.0, 85.0, 85.0, 85.0, 11.76470588235294, 11.707261029411764, 5.4342830882352935], "isController": false}, {"data": ["ADMIN GET reference tipo documento", 1, 0, 0.0, 84.0, 84, 84, 84.0, 84.0, 84.0, 84.0, 11.904761904761903, 13.03245907738095, 5.510602678571428], "isController": false}, {"data": ["ADMIN DELETE item pedido temporal", 1, 0, 0.0, 212.0, 212, 212, 212.0, 212.0, 212.0, 212.0, 4.716981132075471, 4.661704009433962, 2.6809404481132075], "isController": false}, {"data": ["ADMIN GET reference categoria", 1, 0, 0.0, 213.0, 213, 213, 213.0, 213.0, 213.0, 213.0, 4.694835680751174, 6.194065434272301, 2.2144586267605635], "isController": false}, {"data": ["ADMIN POST factura temporal", 1, 0, 0.0, 102.0, 102, 102, 102.0, 102.0, 102.0, 102.0, 9.803921568627452, 16.017539828431374, 6.615732230392157], "isController": false}, {"data": ["ADMIN POST categoria temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 14.539930555555555, 7.096354166666667], "isController": false}, {"data": ["AUTH adminUser", 1, 0, 0.0, 1403.0, 1403, 1403, 1403.0, 1403.0, 1403.0, 1403.0, 0.7127583749109052, 1.077490199572345, 0.19907118674269422], "isController": false}, {"data": ["ADMIN DELETE usuario temporal", 1, 0, 0.0, 79.0, 79, 79, 79.0, 79.0, 79.0, 79.0, 12.658227848101266, 12.33682753164557, 7.132614715189874], "isController": false}, {"data": ["ADMIN POST cuenta temporal", 1, 0, 0.0, 175.0, 175, 175, 175.0, 175.0, 175.0, 175.0, 5.714285714285714, 9.241071428571429, 4.966517857142858], "isController": false}, {"data": ["AUTH managerUser", 1, 0, 0.0, 180.0, 180, 180, 180.0, 180.0, 180.0, 180.0, 5.555555555555555, 8.452690972222223, 1.5625], "isController": false}, {"data": ["ADMIN DELETE pedido temporal", 1, 0, 0.0, 124.0, 124, 124, 124.0, 124.0, 124.0, 124.0, 8.064516129032258, 7.938508064516129, 4.544165826612903], "isController": false}, {"data": ["AUTH userUser", 1, 0, 0.0, 464.0, 464, 464, 464.0, 464.0, 464.0, 464.0, 2.155172413793103, 3.2453865840517238, 0.5998282596982758], "isController": false}, {"data": ["ADMIN DELETE carrito temporal", 1, 0, 0.0, 84.0, 84, 84, 84.0, 84.0, 84.0, 84.0, 11.904761904761903, 11.730375744047619, 6.719680059523809], "isController": false}, {"data": ["ADMIN PUT usuario temporal", 1, 0, 0.0, 85.0, 85, 85, 85.0, 85.0, 85.0, 85.0, 11.76470588235294, 16.957720588235293, 8.467371323529411], "isController": false}, {"data": ["ADMIN PUT categoria temporal", 1, 0, 0.0, 228.0, 228, 228, 228.0, 228.0, 228.0, 228.0, 4.385964912280701, 5.4995888157894735, 3.041049890350877], "isController": false}, {"data": ["ADMIN POST tipo documento temporal", 1, 0, 0.0, 126.0, 126, 126, 126.0, 126.0, 126.0, 126.0, 7.936507936507936, 9.80437748015873, 4.751054067460317], "isController": false}, {"data": ["MANAGER POST autoridad - 403 esperado", 1, 0, 0.0, 80.0, 80, 80, 80.0, 80.0, 80.0, 80.0, 12.5, 13.7451171875, 6.94580078125], "isController": false}, {"data": ["ADMIN PUT pedido temporal", 1, 0, 0.0, 88.0, 88, 88, 88.0, 88.0, 88.0, 88.0, 11.363636363636363, 21.107066761363637, 9.088689630681818], "isController": false}, {"data": ["ADMIN GET reference marca", 1, 0, 0.0, 94.0, 94, 94, 94.0, 94.0, 94.0, 94.0, 10.638297872340425, 11.729138962765957, 4.830867686170213], "isController": false}, {"data": ["ADMIN PUT tipo documento temporal", 1, 0, 0.0, 92.0, 92, 92, 92.0, 92.0, 92.0, 92.0, 10.869565217391305, 12.812075407608695, 7.143766983695652], "isController": false}, {"data": ["ADMIN DELETE etiqueta temporal", 1, 0, 0.0, 79.0, 79, 79, 79.0, 79.0, 79.0, 79.0, 12.658227848101266, 12.584058544303797, 7.268591772151899], "isController": false}, {"data": ["ADMIN DELETE categoria temporal", 1, 0, 0.0, 111.0, 111, 111, 111.0, 111.0, 111.0, 111.0, 9.00900900900901, 8.894636824324325, 5.102759009009009], "isController": false}, {"data": ["CLIENTE B GET cuenta ajena - 403 esperado", 1, 0, 0.0, 91.0, 91, 91, 91.0, 91.0, 91.0, 91.0, 10.989010989010989, 12.53434065934066, 5.354996565934066], "isController": false}, {"data": ["ADMIN PUT precio temporal", 1, 0, 0.0, 88.0, 88, 88, 88.0, 88.0, 88.0, 88.0, 11.363636363636363, 13.327858664772728, 7.2687322443181825], "isController": false}, {"data": ["ADMIN DELETE producto temporal", 1, 0, 0.0, 92.0, 92, 92, 92.0, 92.0, 92.0, 92.0, 10.869565217391305, 10.720957880434783, 6.145974864130435], "isController": false}, {"data": ["ADMIN PUT pago temporal", 1, 0, 0.0, 132.0, 132, 132, 132.0, 132.0, 132.0, 132.0, 7.575757575757576, 11.918501420454545, 5.0233783143939394], "isController": false}, {"data": ["ADMIN POST usuario temporal", 1, 0, 0.0, 211.0, 211, 211, 211.0, 211.0, 211.0, 211.0, 4.739336492890995, 7.127517772511848, 3.3230894549763033], "isController": false}, {"data": ["ADMIN PUT imagen temporal", 1, 0, 0.0, 120.0, 120, 120, 120.0, 120.0, 120.0, 120.0, 8.333333333333334, 13.094075520833334, 6.290690104166667], "isController": false}, {"data": ["ADMIN PUT envio temporal", 1, 0, 0.0, 137.0, 137, 137, 137.0, 137.0, 137.0, 137.0, 7.299270072992701, 11.854185675182482, 5.567119069343065], "isController": false}, {"data": ["AUTH clientAUser", 1, 0, 0.0, 184.0, 184, 184, 184.0, 184.0, 184.0, 184.0, 5.434782608695652, 8.30078125, 1.539147418478261], "isController": false}, {"data": ["ADMIN DELETE direccion temporal", 1, 0, 0.0, 81.0, 81, 81, 81.0, 81.0, 81.0, 81.0, 12.345679012345679, 12.18894675925926, 6.992669753086419], "isController": false}, {"data": ["ADMIN PUT marca temporal", 1, 0, 0.0, 87.0, 87, 87, 87.0, 87.0, 87.0, 87.0, 11.494252873563218, 13.368803879310345, 7.363505747126437], "isController": false}, {"data": ["ADMIN PUT factura temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 17.567274305555557, 8.10546875], "isController": false}, {"data": ["ADMIN POST marca temporal", 1, 0, 0.0, 107.0, 107, 107, 107.0, 107.0, 107.0, 107.0, 9.345794392523365, 11.198525116822431, 5.320896612149533], "isController": false}, {"data": ["ADMIN DELETE pago temporal", 1, 0, 0.0, 81.0, 81, 81, 81.0, 81.0, 81.0, 81.0, 12.345679012345679, 12.12866512345679, 6.93238811728395], "isController": false}, {"data": ["ADMIN POST item carrito temporal", 1, 0, 0.0, 101.0, 101, 101, 101.0, 101.0, 101.0, 101.0, 9.900990099009901, 16.11811571782178, 6.410504331683168], "isController": false}, {"data": ["USER GET cuentas protegidas - 403 esperado", 1, 0, 0.0, 164.0, 164, 164, 164.0, 164.0, 164.0, 164.0, 6.097560975609756, 6.657298018292683, 2.8403677591463414], "isController": false}, {"data": ["ADMIN GET reference producto", 1, 0, 0.0, 98.0, 98, 98, 98.0, 98.0, 98.0, 98.0, 10.204081632653061, 30.93112244897959, 4.952566964285714], "isController": false}, {"data": ["ADMIN PUT cuenta temporal", 1, 0, 0.0, 90.0, 90, 90, 90.0, 90.0, 90.0, 90.0, 11.11111111111111, 17.48046875, 10.3515625], "isController": false}, {"data": ["ADMIN PUT producto temporal", 1, 0, 0.0, 237.0, 237, 237, 237.0, 237.0, 237.0, 237.0, 4.219409282700422, 7.866066719409283, 4.211168248945148], "isController": false}, {"data": ["ADMIN DELETE imagen temporal", 1, 0, 0.0, 86.0, 86, 86, 86.0, 86.0, 86.0, 86.0, 11.627906976744185, 11.537063953488373, 6.6542514534883725], "isController": false}, {"data": ["ADMIN DELETE envio temporal", 1, 0, 0.0, 81.0, 81, 81, 81.0, 81.0, 81.0, 81.0, 12.345679012345679, 12.140721450617283, 6.944444444444445], "isController": false}, {"data": ["ADMIN PUT subcategoria temporal", 1, 0, 0.0, 94.0, 94, 94, 94.0, 94.0, 94.0, 94.0, 10.638297872340425, 14.845827792553191, 7.656665558510638], "isController": false}, {"data": ["ADMIN POST carrito temporal", 1, 0, 0.0, 88.0, 88, 88, 88.0, 88.0, 88.0, 88.0, 11.363636363636363, 17.167524857954547, 6.403142755681818], "isController": false}, {"data": ["ADMIN POST precio temporal", 1, 0, 0.0, 120.0, 120, 120, 120.0, 120.0, 120.0, 120.0, 8.333333333333334, 10.2783203125, 4.866536458333334], "isController": false}, {"data": ["ADMIN POST direccion temporal", 1, 0, 0.0, 105.0, 105, 105, 105.0, 105.0, 105.0, 105.0, 9.523809523809526, 16.19233630952381, 7.40327380952381], "isController": false}, {"data": ["ADMIN PUT item carrito temporal", 1, 0, 0.0, 99.0, 99, 99, 99.0, 99.0, 99.0, 99.0, 10.101010101010102, 15.861742424242424, 7.092408459595959], "isController": false}, {"data": ["ADMIN DELETE factura temporal", 1, 0, 0.0, 80.0, 80, 80, 80.0, 80.0, 80.0, 80.0, 12.5, 12.31689453125, 7.0556640625], "isController": false}, {"data": ["AUTH clientBUser", 1, 0, 0.0, 271.0, 271, 271, 271.0, 271.0, 271.0, 271.0, 3.6900369003690034, 5.63595479704797, 1.0450299815498154], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 73, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
