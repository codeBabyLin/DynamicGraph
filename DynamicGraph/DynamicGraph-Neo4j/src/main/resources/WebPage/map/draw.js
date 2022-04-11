//var RMap = Raphael("map", 600, 500);
window.onload = function () {
    var R = Raphael("map", 600, 500);

    //调用绘制地图方法 
    paintMap(R); 
      //RMap = R;
    var textAttr = { 
        "fill": "#000", 
        "font-size": "12px", 
        "cursor": "pointer" 
    }; 
             
            
    for (var state in china) { 
        china[state]['path'].color = Raphael.getColor(0.9); 
                 
        (function (st, state) { 
             
            //获取当前图形的中心坐标 
            var xx = st.getBBox().x + (st.getBBox().width / 2); 
            var yy = st.getBBox().y + (st.getBBox().height / 2); 
             
            //写入文字
switch (china[state]['name']) { 
                case "江苏": 
                    xx += 5; 
                    yy -= 10; 
                    break; 
                case "河北": 
                    xx -= 10; 
                    yy += 20; 
                    break; 
                case "天津": 
                    xx += 10; 
                    yy += 10; 
                    break; 
                case "上海": 
                    xx += 10; 
                    break; 
                case "广东": 
                    yy -= 10; 
                    break; 
                case "澳门": 
                    yy += 10; 
                    break; 
                case "香港": 
                    xx += 20; 
                    yy += 5; 
                    break; 
                case "甘肃": 
                    xx -= 40; 
                    yy -= 30; 
                    break; 
                case "陕西": 
                    xx += 5; 
                    yy += 10; 
                    break; 
                case "内蒙古": 
                    xx -= 15; 
                    yy += 65; 
                    break; 
                default: 
            } 			
            china[state]['text'] = R.text(xx, yy, china[state]['name']).attr(textAttr); 
             
            st[0].onmouseover = function () {//鼠标滑向 
                st.animate({fill: st.color, stroke: "#eee"}, 500);


                document.getElementById('name').innerHTML = china[state]['name'];
                document.getElementById('code').innerHTML = china[state]['code'];
                document.getElementById('date').innerHTML = molaData[state]['date'];
                document.getElementById('confirmedSum').innerHTML = molaData[state]['confirmedSum'];
                document.getElementById('curedSum').innerHTML = molaData[state]['curedSum'];
                document.getElementById('diedSum').innerHTML = molaData[state]['diedSum'];
                document.getElementById('newConfirmed').innerHTML = molaData[state]['newConfirmed'];
                document.getElementById('newConfirmedLocal').innerHTML = molaData[state]['newConfirmedLocal'];
                china[state]['text'].toFront(); 
                R.safari(); 
            }; 
            st[0].onmouseout = function () {//鼠标离开 
                //st.animate({fill: "#97d6f5", stroke: "#eee"}, 500);
                var color = china[state]['newColor'] ;

                st.animate({fill: color, stroke: "#eee"}, 500);
                china[state]['text'].toFront(); 
                R.safari(); 
            }; 
                     
         })(china[state]['path'], state); 
    }
    init();
} 