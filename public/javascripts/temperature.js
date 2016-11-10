function drawGauge(temp) {
    var Needle, arc, arcEndRad, arcStartRad, barWidth, chart, chartInset, degToRad, el, endPadRad, height, i, margin, needle, numSections, padRad, percToDeg, percToRad, percent, radius, ref, sectionIndx, sectionPerc, startPadRad, svg, totalPercent, width;


    var minTemp = 16.0;

    var maxTemp = 22.0;

    if (temp < minTemp) {
        temp = minTemp;
    } else if (temp > maxTemp) {
        temp = maxTemp;
    }

    percent = (temp - minTemp) / (maxTemp - minTemp);

    var textEl = $('#temperature-text');
    textEl.removeClass('alert-info alert-success alert-danger');
    if (percent < 0.33) {
        textEl.addClass('alert-info');
    } else if (percent > 0.67) {
        textEl.addClass('alert-danger');
    } else {
        textEl.addClass('alert-success');
    }
    barWidth = 40;

    numSections = 3;

    sectionPerc = 1 / numSections / 2;

    padRad = 0.05;

    chartInset = 10;


    totalPercent = 0.75;

    el = d3.select('.chart-gauge');

    margin = {
        top: 20,
        right: 20,
        bottom: 30,
        left: 20
    };

    width = el[0][0].offsetWidth - margin.left - margin.right;

    height = el[0][0].offsetWidth - margin.left - margin.right;

    radius = Math.min(width, height) / 2;

    percToDeg = function (perc) {
        return perc * 360;
    };

    percToRad = function (perc) {
        return degToRad(percToDeg(perc));
    };

    degToRad = function (deg) {
        return deg * Math.PI / 180;
    };

    svg = el.append('svg').attr('width', width + margin.left + margin.right).attr('height', height / 2 + margin.top + margin.bottom);

    chart = svg.append('g').attr('transform', "translate(" + ((width + margin.left) / 2) + ", " + ((height + margin.top) / 2) + ")");

    for (sectionIndx = i = 1, ref = numSections; 1 <= ref ? i <= ref : i >= ref; sectionIndx = 1 <= ref ? ++i : --i) {
        arcStartRad = percToRad(totalPercent);
        arcEndRad = arcStartRad + percToRad(sectionPerc);
        totalPercent += sectionPerc;
        startPadRad = sectionIndx === 0 ? 0 : padRad / 2;
        endPadRad = sectionIndx === numSections ? 0 : padRad / 2;
        arc = d3.svg.arc().outerRadius(radius - chartInset).innerRadius(radius - chartInset - barWidth).startAngle(arcStartRad + startPadRad).endAngle(arcEndRad - endPadRad);
        chart.append('path').attr('class', "arc chart-color" + sectionIndx).attr('d', arc);
    }

    Needle = (function () {
        function Needle(len, radius1) {
            this.len = len;
            this.radius = radius1;
        }

        Needle.prototype.drawOn = function (el, perc) {
            el.append('circle').attr('class', 'needle-center').attr('cx', 0).attr('cy', 0).attr('r', this.radius);
            return el.append('path').attr('class', 'needle').attr('d', this.mkCmd(perc));
        };

        Needle.prototype.animateOn = function (el, perc) {
            var self;
            self = this;
            return el.transition().delay(500).ease('elastic').duration(3000).selectAll('.needle').tween('progress', function () {
                return function (percentOfPercent) {
                    var progress;
                    progress = percentOfPercent * perc;
                    return d3.select(this).attr('d', self.mkCmd(progress));
                };
            });
        };

        Needle.prototype.mkCmd = function (perc) {
            var centerX, centerY, leftX, leftY, rightX, rightY, thetaRad, topX, topY;
            thetaRad = percToRad(perc / 2);
            centerX = 0;
            centerY = 0;
            topX = centerX - this.len * Math.cos(thetaRad);
            topY = centerY - this.len * Math.sin(thetaRad);
            leftX = centerX - this.radius * Math.cos(thetaRad - Math.PI / 2);
            leftY = centerY - this.radius * Math.sin(thetaRad - Math.PI / 2);
            rightX = centerX - this.radius * Math.cos(thetaRad + Math.PI / 2);
            rightY = centerY - this.radius * Math.sin(thetaRad + Math.PI / 2);
            return "M " + leftX + " " + leftY + " L " + topX + " " + topY + " L " + rightX + " " + rightY;
        };

        return Needle;

    })();

    needle = new Needle(90, 15);

    needle.drawOn(chart, 0);

    needle.animateOn(chart, percent);

}

function createTimeLine(values, minTemp, maxTemp) {
    nv.addGraph(function () {
        chart = nv.models.lineChart()
            .options({
                useInteractiveGuideline: false
            });

        chart.xAxis     //Chart x-axis settings
            .tickFormat(function (d) {
                return d3.time.format('%d/%m %H:%M')(new Date(d));
            })
            .staggerLabels(false);

        chart.yAxis     //Chart y-axis settings
            .axisLabel('Temperatur')
            .tickFormat(d3.format('.02f'));
        var maxLine = [];
        var minLine = [];
        for (var i = 0; i < values; i++) {
            maxLine.push({x: values[i].x, y: maxTemp});
            minLine.push({x: values[i].x, y: minTemp});
        }

        var data = [{
            values: values,
            key: 'Temperatur',
            color: '#984b43'
        }, {
            values: maxLine,
            key: 'max',
            color: '#eac67a'
        }, {
            values: minLine,
            key: 'min',
            color: '#eac67a'
        }];

        d3.select('#chart').append('svg')
            .datum(data)
            .transition().duration(500)
            .call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });

}