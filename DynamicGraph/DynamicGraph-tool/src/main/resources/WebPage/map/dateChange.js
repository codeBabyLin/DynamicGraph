var molaData = [];
function change() {
	var value = document.getElementById('range').value ;
	var xhr = new XMLHttpRequest();
	var pathname = window.location.href;
	var date = myCars[value]
	var newPathName = pathname + "date/"+date;
	xhr.open('GET',newPathName);
	//'code':510000000000,'date':2022.2.4,'confirmedSum':1374,'curedSum':1347,'diedSum':3,'newConfirmed':0,'newConfirmedLocal':0
	xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            var datastr = xhr.responseText;
            var data = datastr.split("#");
            var newPdata = new Array();
            for(var i in data){

                var p = data[i];

                var ps = p.split(",");


               var o = ps[0].split(":");
               newPdata[0] = o[1];

                o = ps[1].split(":");
                newPdata[1] = o[1];

                o = ps[2].split(":");
                newPdata[2] = o[1];

                o = ps[3].split(":");
                newPdata[3] = o[1];

                o = ps[4].split(":");
                newPdata[4] = o[1];

                o = ps[5].split(":");
                newPdata[5] = o[1];

                o = ps[6].split(":");
                newPdata[6] = o[1];
                var c = newPdata[0]
                var s = newCode[c]
                var dte = newPdata[1];

                //var pql = dte;
                molaData[s] = {};
                molaData[s]['date'] = dte;
                molaData[s]['confirmedSum'] = parseInt(newPdata[2]);
                molaData[s]['curedSum'] = parseInt(newPdata[3]);
                molaData[s]['diedSum'] = parseInt(newPdata[4]);
                molaData[s]['newConfirmed'] = parseInt(newPdata[5]);
                molaData[s]['newConfirmedLocal'] = parseInt(newPdata[6]);


                console.info(p);
            }
            redrawMap(molaData);

        }
    }
    xhr.send();

	console.info(newPathName);


	document.getElementById('value').innerHTML = myCars[value];
}

function redrawMap(molaData){

        var sixLevel = "#ff0000";
        var fifthLevel = "#ff4f4f";
        var fouthLevel = "#fe8383";
        var thirdLevel = "#ffb8b8";
        var secondLevel = "#ffe6e6";
        var firstLevel = "#ffffff";
for (var state in china) {

        if(state == 'aomen' || state == 'hk' || state == 'taiwan'){
            continue;
        }

        var st = china[state]['path'];

        var data = molaData[state];
        var consum = data['confirmedSum'];
        var curedSum = data['curedSum'];
        var diedSum = data['diedSum'];


        var size = consum -curedSum -diedSum;
        var color = firstLevel;
        if(size > 10000){
            color = sixLevel;
        }
        else if(size > 5000){
            color = fifthLevel;
        }
        else if (size > 1000){
            color = fouthLevel;
        }
        else if (size >200){
            color = thirdLevel;
        }
        else if (size >10){
            color = secondLevel;
        }
        else{
            color = firstLevel;
        }


        st.animate({fill: color, stroke: "#eee"}, 500);
        china[state]['newColor'] = color;
        //'code':510000000000,'date':2022.2.4,'confirmedSum':1374,'curedSum':1347,'diedSum':3,'newConfirmed':0,'newConfirmedLocal':0
        china[state]['date'] = molaData['date'];
         china[state]['confirmedSum'] = molaData['confirmedSum'];
          china[state]['curedSum'] = molaData['curedSum'];
           china[state]['diedSum'] = molaData['diedSum'];
            china[state]['newConfirmed'] = molaData['newConfirmed'];
             china[state]['newConfirmedLocal'] = molaData['newConfirmedLocal'];

        //china[state]['text'] = size;
        china[state]['text'].toFront();
        //RMap.safari();

    }
}

