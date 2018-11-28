/* 
 * Citus WebGIS client javascript library
 *
 * Copyright (c) 2014 by Citus Co. LTD
 *
 */


(function() {
    Proj4js.defs['EPSG:5187'] = '+title=GRS_80_EAST_60 +proj=tmerc +lat_0=38 +lon_0=129 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs';
    Proj4js.defs['EPSG:5186'] = '+title=GRS_80_EAST_60 +proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs';
    Proj4js.defs['EPSG:5179'] = '+title=GRS_80_EAST_60 +proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs';
    Proj4js.defs['EPSG:5185'] = '+title=GRS_80_EAST_60 +proj=tmerc +lat_0=38 +lon_0=127.5 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs';
    Proj4js.defs['EPSG:4737'] = '+proj=longlat +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +no_defs'; // Korean2000.LL
    Proj4js.defs['EPSG:4019'] = '+proj=longlat +ellps=GRS80 +no_defs';

    /**
     * Namespace: citus.WebGIS
     * The citus.WebGIS object provides a namespace for all things
     */
    var $WebGIS_proto = {
        lon: 128.52,
        lat: 37.22,
        initialZoom: 2,
        map: null,
        maxZoomLevel:13,
        visibleZoomLevel: 6,
        epsg5187: null,
        epsg5186: null,
        epsg5179: null,
        epsg4326: null,
        epsg4019: null,
        epsg900913: null,
        epsgProj: null,
        epsgCode: '5186',
        epsgBase: null,
        styleContext: null,
        scalebar: null,
        navToolbar: null,
        editorToolbar: null,
        editor: null,
        layerSwitcher: null,
        history: null,
        webgisPrefix: null,
        shapePrefix: null,
        options: {
            url: {
                webgis: 'http://localhost:8080/gis/citus/',
                shape: 'http://localhost:8080/gis/'
            },
            basemap: {
                source: 'localmap', // or 'vworldmap'
                layer: 'YJ-gun',
                url: 'https://gis.yeongwol.or.kr/geoserver'
            },
            toolbar: {
                element: null
            },
            editToolbar: {
                element: null
            },
            layerSwitcher: {
                element: null
            },
            handlers: {
                onSelect: function() {},
                onUnselect: function() {}
            },
            measures: {
                dist: function(m, u) {},
                area: function(m, u) {}
            },
            snaping: {
                layerName: '',
                tolerance: 10
            }
        },
        LAYER: { // 나-라.상수및공통테이블(p4-64).doc
            WTL_PIPE_LM: {
                SA001: "WTL_PIPE_LM.SA001" // 상수관로
            },
            WTL_PIPE_PS: {
                SA900: "WTL_PIPE_PS.SA900" // 상수관로심도
            },
            WTL_MANH_PS: {
                SA100: "WTL_MANH_PS.SA100", // 상수맨홀
                SA991: "WTL_MANH_PS.SA991" // 신축관실
            },
            WTL_SPLY_LS: {
                SA002: "WTL_SPLY_LS.SA002" // 급수관로
            },
            WTL_STPI_PS: {
                SA003: "WTL_STPI_PS.SA003" // 스탠드파이프
            },
            WTL_VALV_PS: {
                SA200: "WTL_VALV_PS.SA200", // 제수변
                SA201: "WTL_VALV_PS.SA201", // 역지변
                SA202: "WTL_VALV_PS.SA202", // 이토변
                SA203: "WTL_VALV_PS.SA203", // 배기변
                SA204: "WTL_VALV_PS.SA204", // 감압변
                SA205: "WTL_VALV_PS.SA205" // 안전변
            },
            WTL_FLOW_PS: {
                SA117: "WTL_FLOW_PS.SA117" // 유량계
            },
            WTL_PRGA_PS: {
                SA121: "WTL_PRGA_PS.SA121" // 수압계
            },
            WTL_FIRE_PS: {
                SA118: "WTL_FIRE_PS.SA118", // 급수탑
                SA119: "WTL_FIRE_PS.SA119" // 소화전
            },
            WTL_LEAK_PS: {
                SA300: "WTL_LEAK_PS.SA300" // 누수지점
            },
            WTL_PRES_PS: {
                SA206: "WTL_PRES_PS.SA206" // 가압장
            },
            WTL_PURI_AS: {
                SA113: "WTL_PURI_AS.SA113" // 정수장
            },
            WTL_SERV_PS: {
                SA114: "WTL_SERV_PS.SA114" // 배수지
            },
            WTL_HEAD_PS: {
                SA110: "WTL_HEAD_PS.SA110" // 수원지
            },
            WTL_GAIN_PS: {
                SA112: "WTL_GAIN_PS.SA112" // 취수장
            },
            WTL_META_PS: {
                SA122: "WTL_META_PS.SA122" // 급수전계량기
            },
            WTL_WTQT_PS: {
                SA123: 'WTL_WTQT_PS.SA123' // 수질계
            },
            WTL_RDCG_PS: {
                SA224: 'WTL_RDCG_PS.SA224' // 감압장
            },
            WTL_CTPN_PS: {
                SA992: 'WTL_CTPN_PS.SA992' // 제어반
            },
            WTL_BLOK_AS: {
                SA993: 'WTL_BLOK_AS.SA993' // 블록
            },
            WTL_RSRV_PS: {
                SA120: "WTL_RSRV_PS.SA120" // 저수조
            }
        },
        FEATURE: {
            'WTL_PIPE_LM.SA001': {
                type: 'path'
            },
            'WTL_SPLY_LS.SA002': {
                type: 'path'
            },
            'WTL_PIPE_PS.SA900': {
                type: 'point'
            },
            'WTL_MANH_PS.SA100': {
                type: 'point'
            },
            'WTL_MANH_PS.SA991': {
                type: 'point'
            },
            'WTL_STPI_PS.SA003': {
                type: 'point'
            },
            'WTL_VALV_PS.SA200': {
                type: 'point'
            },
            'WTL_VALV_PS.SA201': {
                type: 'point'
            },
            'WTL_VALV_PS.SA202': {
                type: 'point'
            },
            'WTL_VALV_PS.SA203': {
                type: 'point'
            },
            'WTL_VALV_PS.SA204': {
                type: 'point'
            },
            'WTL_VALV_PS.SA205': {
                type: 'point'
            },
            'WTL_FLOW_PS.SA117': {
                type: 'point'
            },
            'WTL_PRGA_PS.SA121': {
                type: 'point'
            },
            'WTL_FIRE_PS.SA118': {
                type: 'point'
            },
            'WTL_FIRE_PS.SA119': {
                type: 'point'
            },
            'WTL_LEAK_PS.SA300': {
                type: 'point'
            },
            'WTL_PRES_PS.SA206': {
                type: 'point'
            },
            'WTL_PURI_AS.SA113': {
                type: 'polygon'
            },
            'WTL_SERV_PS.SA114': {
                type: 'point'
            },
            'WTL_HEAD_PS.SA110': {
                type: 'point'
            },
            'WTL_GAIN_PS.SA112': {
                type: 'point'
            },
            'WTL_META_PS.SA122': {
                type: 'point'
            },
            'WTL_WTQT_PS.SA123': {
                type: 'point'
            },
            'WTL_RDCG_PS.SA224': {
                type: 'point'
            },
            'WTL_CTPN_PS.SA992': {
                type: 'point'
            },
            'WTL_BLOK_AS.SA993': {
                type: 'polygon'
            },
            'WTL_RSRV_PS.SA120': {
                type: 'point'
            }
        },
        STYLE: {
            'WTL_PIPE_LM.SA001': {
                /*defaultLabel: {}, selectLabel: {},       // defaultLabel and selectLabel Styles are needed for DrawText Control*/
                'default': {
                    label: '${label}',
                    fillColor: "#ff0000",
                    fillOpacity: 0.8,
                    strokeColor: '#00f',
                    strokeWidth: 4,
                    pointRadius: 5,
                },
                select: {
                    strokeWidth: 4,
                    fillColor: "#00ff00",
                    strokeColor: '#f70'
                },
                temporary: {
                    fillColor: "#0000ff",
                    fillOpacity: 1,
                    strokeWidth: 5,
                    pointRadius: 10
                }
            },
            'WTL_PIPE_PS.SA900': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_MANH_PS.SA100': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_MANH_PS.SA991': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_SPLY_LS.SA002': {
                'default': {
                    label: '${label}',
                    strokeColor: '#00f',
                    strokeWidth: 1,
                    pointRadius: 5,
                },
                select: {
                    strokeWidth: 3,
                    fillColor: "#00ff00",
                    strokeColor: '#f70'
                },
                temporary: {
                    fillColor: "#0000ff",
                    fillOpacity: 1,
                    strokeWidth: 5,
                    pointRadius: 10
                }
            },
            'WTL_STPI_PS.SA003': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA200': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA201': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA202': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA203': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA204': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_VALV_PS.SA205': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_FLOW_PS.SA117': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_PRGA_PS.SA121': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_FIRE_PS.SA118': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_FIRE_PS.SA119': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WML_LEAK_PS.SA300': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_PRES_PS.SA206': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_PURI_AS.SA113': {
                'default': {
                    label: '${label}',
                    fillColor: '#07f',
                    fillOpacity: 0.8,
                    strokeColor: '#037',
                    strokeWidth: 2,
                    pointRadius: 5
                },
                select: {
                    fillColor: '#fc0',
                    strokeColor: '#f70'
                },
                temporary: {
                    fillColor: '#07f',
                    fillOpacity: 0.8,
                    strokeWidth: 1,
                    pointRadius: 5
                }
            },
            'WTL_SERV_PS.SA114': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_HEAD_PS.SA110': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_GAIN_PS.SA112': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_META_PS.SA122': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_WTQT_PS.SA123': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_RDCG_PS.SA224': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_CTPN_PS.SA992': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            },
            'WTL_BLOK_AS.SA993': {
                'default': {
                    label: '${label}',
                    fillColor: '#07f',
                    fillOpacity: 0.8,
                    strokeColor: '#037',
                    strokeWidth: 2,
                    pointRadius: 5
                },
                select: {
                    fillColor: '#fc0',
                    strokeColor: '#f70'
                },
                temporary: {
                    fillColor: '#07f',
                    fillOpacity: 0.8,
                    strokeWidth: 1,
                    pointRadius: 5
                }
            },         
            'WTL_RSRV_PS.SA120': {
                'default': {
                    label: '${label}',
                    externalGraphic: '${icon}',
                    graphicWidth: 15,
                    graphicHeight: 15,
                    rotation: 0
                },
                select: {
                    graphicWidth: 20,
                    graphicHeight: 20,
                    rotation: 0
                },
                temporary: {}
            }
        }
    };

    var $WebGIS = function(element, options) {

        citus.extend(this.options, options);

        this.load = function(callback) {
            //citus.loadJS('http://openlayers.org/api/OpenLayers.js', function() { 
            //    citus.loadJS('http://map.vworld.kr/js/apis.do?type=Base&apiKey=F2F4AAFD-BC8E-3B3A-8C6B-7433BD0330C2', function() {
            this.init();
            if (typeof callback !== 'undefined') {
                citus.task(function() {
                    callback.apply(this, arguments);
                }, this);
            }
            //    }.bind(this));
            //}.bind(this));

            return this;
        };

        this.init = function() {
            this.webgisPrefix = this.options.url.webgis;
            this.shapePrefix = this.options.url.shape;

            this.epsg5187 = new OpenLayers.Projection("EPSG:5187");
            this.epsg5186 = new OpenLayers.Projection("EPSG:5186");
            this.epsg4326 = new OpenLayers.Projection("EPSG:4326");
            this.epsg900913 = new OpenLayers.Projection("EPSG:900913"); // EPSG:3857
            this.epsg4019 = new OpenLayers.Projection("EPSG:4019");
            this.maxBound = new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34);

            this.epsgProj = this['epsg' + this.epsgCode];
            this.epsgBase = this.epsg900913;

            this.firstArcize = true;

            var styleColor = function(f) {
                return '#ff0000';
            }.bind(this);

            var styleHide = function(f) {
                var visibleLevel = this.visibleZoomLevel;
                if (typeof f.layer !== 'undefined' && f.layer != null) {
                    if (typeof this.STYLE[f.layer.name]['default'].visibleZoomLevel !== 'undefined' && this.STYLE[f.layer.name]['default'].visibleZoomLevel !== -1) {
                        visibleLevel = this.STYLE[f.layer.name]['default'].visibleZoomLevel;
                    }
                }
                return this.map.getZoom() < visibleLevel ? "none" : "";
            }.bind(this);

            var styleSize = function(f) {
                return Math.max(this.map.getZoom() - 10, 3);
            }.bind(this);

            this.styleContext = {
                'default': {
                    label: function(f) {
                        return '';
                    },
                    color: styleColor,
                    hide: styleHide,
                    size: styleSize,
                    icon: function(f) {
                        if (f.geometry instanceof OpenLayers.Geometry.Point) {
                            return this.webgisPrefix + 'images/' + f.layer.layerCode + '.gif';
                        } else {
                            return null;
                        }
                    }.bind(this)
                },
                select: {
                    color: styleColor,
                    hide: styleHide,
                    size: styleSize
                }
            };

            var styleBase = {
                'default': {
                    display: "${hide}",
                    graphicZIndex: 1
                },
                select: {
                    graphicZIndex: 2
                },
                temporary: {
                    graphicZIndex: 2
                }
            };

            for (var s in this.STYLE) {
                citus.extend(this.STYLE[s], styleBase);
            }

            // style measure sketch
            var styleSketch = new OpenLayers.Style();
            styleSketch.addRules([
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 4,
                            graphicName: "square",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 1,
                            strokeOpacity: 1,
                            strokeColor: "#333333"
                        },
                        "Line": {
                            strokeWidth: 3,
                            strokeOpacity: 1,
                            strokeColor: "#666666",
                            strokeDashstyle: "dash"
                        },
                        "Polygon": {
                            strokeWidth: 2,
                            strokeOpacity: 1,
                            strokeColor: "#666666",
                            fillColor: "white",
                            fillOpacity: 0.3
                        }
                    }
                })
            ]);
            var styleSketchMap = new OpenLayers.StyleMap({
                'default': styleSketch
            });

            var measureHandlerOptions = {
                persist: true,
                handlerOptions: {
                    layerOptions: {
                        renderers: OpenLayers.Layer.Vector.prototype.renderers,
                        styleMap: styleSketchMap
                    }
                }
            };

            var zoomInBoxControl = new OpenLayers.Control.ZoomBox({
                out: false,
                zoomOnClick: true
            });
            var zoomOutBoxControl = new OpenLayers.Control.ZoomBox({
                out: true,
                zoomOnClick: true
            });
            var zoomBoxFunc = function(position) {
                if (position instanceof OpenLayers.Bounds) {
                    var bounds,
                        targetCenterPx = position.getCenterPixel();
                    if (!this.out) {
                        var minXY = this.map.getLonLatFromPixel({
                            x: position.left,
                            y: position.bottom
                        });
                        var maxXY = this.map.getLonLatFromPixel({
                            x: position.right,
                            y: position.top
                        });
                        bounds = new OpenLayers.Bounds(minXY.lon, minXY.lat,
                            maxXY.lon, maxXY.lat);
                    } else {
                        var pixWidth = position.right - position.left;
                        var pixHeight = position.bottom - position.top;
                        var zoomFactor = Math.min((this.map.size.h / pixHeight), (this.map.size.w / pixWidth));
                        var extent = this.map.getExtent();
                        var center = this.map.getLonLatFromPixel(targetCenterPx);
                        var xmin = center.lon - (extent.getWidth() / 2) * zoomFactor;
                        var xmax = center.lon + (extent.getWidth() / 2) * zoomFactor;
                        var ymin = center.lat - (extent.getHeight() / 2) * zoomFactor;
                        var ymax = center.lat + (extent.getHeight() / 2) * zoomFactor;
                        bounds = new OpenLayers.Bounds(xmin, ymin, xmax, ymax);
                    }
                    // always zoom in/out 
                    var lastZoom = this.map.getZoom(),
                        size = this.map.getSize(),
                        centerPx = {
                            x: size.w / 2,
                            y: size.h / 2
                        },
                        zoom = this.map.getZoomForExtent(bounds),
                        oldRes = this.map.getResolution(),
                        newRes = this.map.getResolutionForZoom(zoom);
                    if (oldRes == newRes) {
                        this.map.setCenter(this.map.getLonLatFromPixel(targetCenterPx));
                    } else {
                        var zoomOriginPx = {
                            x: (oldRes * targetCenterPx.x - newRes * centerPx.x) /
                                (oldRes - newRes),
                            y: (oldRes * targetCenterPx.y - newRes * centerPx.y) /
                                (oldRes - newRes)
                        };
                        this.map.zoomTo(Math.round(zoom), zoomOriginPx); // <--- bug fixed
                    }
                    if (lastZoom == this.map.getZoom() && this.alwaysZoom == true) {
                        this.map.zoomTo(lastZoom + (this.out ? -1 : 1));
                    }
                } else if (this.zoomOnClick) { // it's a pixel
                    if (!this.out) {
                        this.map.zoomTo(this.map.getZoom() + 1, position);
                    } else {
                        this.map.zoomTo(this.map.getZoom() - 1, position);
                    }
                }
            };
            zoomInBoxControl.zoomBox = zoomOutBoxControl.zoomBox = zoomBoxFunc;


            OpenLayers.Control.Transection = OpenLayers.Class(OpenLayers.Control.DrawFeature, {
                title: 'Transection',

                initialize: function(layer, options) {
                    OpenLayers.Control.DrawFeature.prototype.initialize.apply(this, [layer, OpenLayers.Handler.Path, options]);
                    this.events.register('activate', this, this.test);

                },

                test: function() {},

                drawFeature: function(geometry) {
                    var cut = new OpenLayers.Feature.Vector(geometry);
                    var proceed = this.layer.events.triggerEvent(
                        'sketchcomplete', {
                            feature: cut
                        }
                    );
                    this.deactivate();
                    if (proceed !== false) {
                        //var wktFormat = new OpenLayers.Format.WKT();
                        var selected = this.map.webgis.findIntersectedFeatures(this.layer.features, cut);
                        var cutNodes = cut.geometry.getVertices(true);

                        OpenLayers.Request.POST({
                            url: this.map.webgis.shapePrefix + 'FindIntersectionsGeometry.json.do',
                            data: OpenLayers.Util.getParameterString({
                                cut: new OpenLayers.Format.GeoJSON().write(cut),
                                geo: new OpenLayers.Format.GeoJSON().write(selected)
                            }),
                            headers: {
                                "Content-Type": "application/x-www-form-urlencoded"
                            },
                            callback: function(response) {
                                if (response.status == 200) {
                                    var result = eval('(' + response.responseText + ')');
                                    result.start = {
                                        x: cutNodes[0].x,
                                        y: cutNodes[0].y
                                    };
                                    result.end = {
                                        x: cutNodes[1].x,
                                        y: cutNodes[1].y
                                    };

                                    citus.task(function() {
                                        this.result_callback.apply(this, [{
                                            message: 'success',
                                            features: result
                                        }]);
                                    }, this);
                                } else {
                                    citus.task(function() {
                                        this.result_callback.apply(this, [{
                                            message: 'fail'
                                        }]);
                                    }, this);
                                }
                            },
                            scope: this
                        });
                    } else {
                        citus.task(function() {
                            this.result_callback.apply(this, [{
                                message: 'fail'
                            }]);
                        }, this);
                    }
                },

                CLASS_NAME: 'OpenLayers.Control.Transection'
            });

            //Creation of a custom panel with a ZoomBox control with the alwaysZoom option sets to true             
            OpenLayers.Control.CitusNavToolbar = OpenLayers.Class(OpenLayers.Control.Panel, {
                initialize: function(options) {
                    OpenLayers.Control.Panel.prototype.initialize.apply(this, [options]);
                    this.addControls([
                        new OpenLayers.Control.Navigation(),
                        zoomInBoxControl,
                        zoomOutBoxControl,
                        new OpenLayers.Control.Measure(OpenLayers.Handler.Path, measureHandlerOptions),
                        new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, measureHandlerOptions)
                    ]);
                    // To make the custom navToolbar use the regular navToolbar style
                    //this.displayClass = 'olControlnavToolbar'
                    this.defaultControl = this.controls[0];
                },

                /**
                 * Method: draw
                 * calls the default draw, and then activates mouse defaults.
                 */
                draw: function() {
                    var div = OpenLayers.Control.Panel.prototype.draw.apply(this, arguments);
                    //this.defaultControl = this.controls[0];
                    return div;
                },

                deactivateControls: function() {
                    var len = this.controls.length;
                    for (var i = 0; i < len; i++) {
                        this.controls[i].deactivate();
                    }
                    if (this.highlightCtrl) {
                        this.highlightCtrl.unselectAll();
                        this.highlightCtrl.deactivate();
                    }
                    if (this.selectCtrl) {
                        this.selectCtrl.unselectAll();
                        this.selectCtrl.deactivate();
                    }
                },
                activateDefaultControl: function() {
                    this.defaultControl !== null ? this.defaultControl.activate() : null;
                },
                activateSelectionControl: function() {
                    this.highlightCtrl ? this.highlightCtrl.activate() : null;
                    this.selectCtrl ? this.selectCtrl.activate() : null;
                }
            });

            this.navToolbar = new OpenLayers.Control.CitusNavToolbar(this.options.toolbar.element != null ? {
                div: OpenLayers.Util.getElement(this.options.toolbar.element)
            } : {}); // http://dev.openlayers.org/releases/OpenLayers-2.13.1/examples/navToolbar-outsidemap.html

            var measureHandler = function(e) {
                //var ctrl = null;
                if (e.order == 1) {
                    //ctrl = this.navToolbar.controls[3];
                    this.options.measures.dist.apply(this, [e.measure, e.units]);
                } else {
                    //ctrl = this.navToolbar.controls[4];
                    this.options.measures.area.apply(this, [e.measure, e.units]);
                }

            }.bind(this);

            this.navToolbar.controls[3].events.on({
                measure: measureHandler
            });
            this.navToolbar.controls[4].events.on({
                measure: measureHandler
            });

            if (this.options.basemap.source === 'localmap') {
            	this.mapOptions = {
                    //projection: this.epsg5187,
                    projection: this.epsgBase,
                    // http://geoanalytics.renci.org/uncategorized/more-about-the-map-object/
                    // http://trac.osgeo.org/openlayers/wiki/SettingZoomLevels
                    //units: "degree",
                    //scales: [250000, 50000, 25000, 5000, 2500, 500],
                    //maxScale : 250000,
                    //minScale : 2500,
                    numZoomLevels: this.maxZoomLevel + 1,
                    //maxResolution: 39135.7584765625,
                    //minResolution: 0.2985821416974068,
                    //maxExtent: this.maxBound,
                    //minExtent: "auto",
                    //restrictedExtent : this.maxBound
                    //numZoomLevels : this.maxZoomLevel,
                    //fractionalZoom : true,        --> It affects mouse wheel.
                    webgis: this
                };
            } else {
            	this.mapOptions = {
                    //projection: this.epsg5187,
                    projection: this.epsgBase,
                    // http://geoanalytics.renci.org/uncategorized/more-about-the-map-object/
                    // http://trac.osgeo.org/openlayers/wiki/SettingZoomLevels
                    //units: "degree",
                    //scales: [250000, 50000, 25000, 5000, 2500, 500],
                    //maxScale : 250000,
                    //minScale : 2500,
                    //numZoomLevels : this.maxZoomLevel + 1,
                    //maxResolution: 39135.7584765625,
                    //minResolution: 0.2985821416974068,
                    //maxExtent: this.maxBound,
                    //minExtent: "auto",
                    //restrictedExtent : this.maxBound
                    //numZoomLevels : this.maxZoomLevel,
                    fractionalZoom: true,
                    webgis: this
                };
            }
            this.map = new OpenLayers.Map(element, this.mapOptions);
            
            var osmOptions = {
                // 22-level resolutions
                /*
                resolutions: [156543.03390625, 78271.516953125, 39135.7584765625, 19567.87923828125, 9783.939619140625, 4891.9698095703125, 2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135],//, 0.25],//, 0.1], 0.05],
                //*/
                //*
                isBaseLayer: false,
                visibility: false,
                resolutions: [156543.03390625, 78271.516953125, 39135.7584765625, 19567.87923828125, 9783.939619140625, 4891.9698095703125, 2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135, 0.25, 0.1, 0.05],
                //*/
                // 12-level server resolutions
                serverResolutions: [156543.03390625, 78271.516953125, 39135.7584765625, 19567.87923828125, 9783.939619140625, 4891.9698095703125, 2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135],
                //zoomOffset : 13,
                //numZoomLevels : this.maxZoomLevel,
                transitionEffect: 'resize'
            };

            var osm = new OpenLayers.Layer.OSM("OSM", null, osmOptions);
            osm.attribution = "";

            var vwOptions = {
                serviceVersion: "",
                layername: "",
                isBaseLayer: false,
                visibility: false,
                opacity: 1,
                type: 'png',
                transitionEffect: 'resize',
                tileSize: new OpenLayers.Size(256, 256),
                min_level: 7,
                max_level: 18,
                buffer: 0
            };

            //var vwSatellite = new vworld.Layers.Satellite('VBASE', vwOptions);
            var vwBase = new vworld.Layers.Base('VBASE', vwOptions);
            var markers = new OpenLayers.Layer.Markers("MARKERS");
            /*
            var wfs = new OpenLayers.Layer.WFS("VWORLD", 'http://map.vworld.kr/js/wfs.do?SERVICE=WFS&REQUEST=GetFeature&TYPENAME=LT_C_UQ111&BBOX=13987670,3912271,14359383,4642932&VERSION=1.1.0&MAXFEATURES=40&SRSNAME=EPSG:900913&OUTPUT=text/xml;subType=gml/3.1.1/profiles/gmlsf/1.0.0/0&EXCEPTIONS=text/xml&APIKEY=4FAC4DA4-D9B1-3CE5-891C-CE3664473E79&DOMAIN=http://211.106.178.15/'
            );
            //*/

            this.geoserverOptions = {
                isBaseLayer: false,
                visibility: false,
                numZoomLevels : this.maxZoomLevel + 1,
                //resolutions: [156543.03390625, 78271.516953125, 39135.7584765625, 19567.87923828125, 9783.939619140625, 4891.9698095703125, 2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135, 0.25, 0.1, 0.05, 0.025],
                resolutions: [305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135, 0.25, 0.1, 0.05, 0.025],
                maxResolution: 305.74811309814453, 
                minResolution: 0.025,
                maxExtent: this.maxBound
                //minExtent: "auto",
            };

            this.geoserver = new OpenLayers.Layer.WMS('geoserver', this.options.basemap.url + '/CITUS/wms', {
                LAYERS: this.options.basemap.layer, //'YJ-gun'
                STYLES: '',
                format: 'image/png',
                tiled: 'true'
            }, this.geoserverOptions);

            if (this.options.basemap.source === 'localmap') {
                //osm.setIsBaseLayer(true);
                this.geoserver.setIsBaseLayer(true);
                this.map.addLayers([
                    //osm,
                    this.geoserver,
                    //vwBase,
                    markers
                ]);
                this.map.setBaseLayer(this.geoserver);
                this.map.baseLayer.setVisibility(true);
            } else {
                //osm.setIsBaseLayer(true);
                vwBase.setIsBaseLayer(true);
                this.map.addLayers([
                    osm,
                    vwBase,
                    //this.geoserver,
                    markers
                ]);
                //this.map.setBaseLayer(osm);
                this.map.setBaseLayer(vwBase);
                this.map.baseLayer.setVisibility(true);
            }
            
            
            /*재정의 주석처리.
            2015.07.01 by lispee
            (For Indexmap이 이동하지 않는 오류 방지)*/
            /*this.map.setCenter = function(lonlat, zoom, dragging, forceZoomChange) {
                if (this.panTween) {
                    this.panTween.stop();
                }
                if (this.zoomTween) {
                    this.zoomTween.stop();
                }
                this.moveTo(lonlat, Math.round(zoom), {
                    'dragging': dragging,
                    'forceZoomChange': forceZoomChange
                });
            };*/

            // avoid pink tiles
            /*
            OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
            OpenLayers.Util.onImageLoadError = function(){
                this.src = "images/blank.png";
            };            
            OpenLayers.Util.onImageLoadErrorColor = "transparent";
            */       	
            
            OpenLayers.Control.Navigation.prototype.counter = 0;
            OpenLayers.Control.Navigation.prototype.wheelUp = function(evt) {
                this.counter++;
                if (this.counter > 2) {
                    this.counter = 0;
                    this.wheelChange(evt, 1);
                }
            };
            OpenLayers.Control.Navigation.prototype.wheelDown = function(evt) {
                this.counter--;
                if (this.counter < -2) {
                    this.counter = 0;
                    this.wheelChange(evt, -1);
                }
            };
            
            var overviewOptions = {
	        	size: {
	                w: 300,
	                h: 200
	            },
	            maximized: false,
            	minRatio: 1, 
            	maxRatio: this.geoserverOptions.maxResolution/this.geoserverOptions.minResolution,
	            mapOptions: {
	            	restrictedExtent: new OpenLayers.Bounds(this.minx, this.miny, this.maxx, this.maxy).transform(this.epsg4326, this.epsgBase),
	            	numZoomLevels: 1,
	            	autoPan: true
	            }
	        };
            
            this.overview = new OpenLayers.Control.OverviewMap(overviewOptions)
            this.map.addControl(this.overview);
            
            this.map.addControls([
                this.navToolbar,
                //this.layerSwitcher = new OpenLayers.Control.LayerSwitcher(this.options.layerSwitcher.element != null ? {
                //    div : OpenLayers.Util.getElement(this.options.layerSwitcher.element)
                //} : {}),    // http://gis.stackexchange.com/questions/28481/custom-layer-switcher-style-in-openlayers
                new OpenLayers.Control.LayerSwitcher(),
                this.history = new OpenLayers.Control.NavigationHistory(), // http://openlayers.org/dev/examples/navigation-history.html
                //new OpenLayers.Control.PanZoomBar(),
                //new OpenLayers.Control.PanZoom(),
                new OpenLayers.Control.Navigation({
                    mouseWheelOptions: {
                        cumulative: false,
                        interval: 100,
                        maxDelta: 1
                    }
                }),
                //this.overview = new OpenLayers.Control.OverviewMap(overviewOptions),
                //new OpenLayers.Control.Graticule(),
                new OpenLayers.Control.Attribution(),
                new OpenLayers.Control.ScaleLine({
                    bottomOutUnits: "",
                    bottomInUnits: "",
                    maxWidth: 200
                }),
                //scalebar = new OpenLayers.Control.ScaleBar(),
                //new OpenLayers.Control.Permalink('permalink'),
                new OpenLayers.Control.KeyboardDefaults(),
                new OpenLayers.Control.MousePosition({
                    prefix: 'EPSG:' + this.epsgCode + ' 좌표: ',
                    separator: ' | ',
                    numDigits: 2,
                    emptyString: '',
                    displayProjection: this.epsgProj
                })
            ]);
            
            // http://hslayers.org/documentation/openlayers/events.html
            /*
            this.map.events.register("mousemove", this, function(e) {
                var position = this.map.events.getMousePosition(e);
                OpenLayers.Util.getElement("coords").innerHTML = position;
            });
            */
            var mapEventHandler = function(e) {
                var scale = $("#scale");
                if (scale != null) this.displayScale(scale);
                // request features bounded by current map extent via $ajax
                if (this.editor == null) {
                    var extent = this.map.getExtent().transform(this.epsgBase, this.epsgProj); // OpenLayers.Bounds

                    var layers = this.map.getLayersBy("visibility", true);
                    var zoomLevel = this.map.getZoom();
                    for (var i = 0, len = layers.length; i < len; i++) {
                        var visibleLevel = this.visibleZoomLevel;
                        if (layers[i].name !== 'OSM' && layers[i].name !== 'VBASE' && layers[i].name !== 'MARKERS' && layers[i].name !== 'geoserver') {
                            if (typeof this.STYLE[layers[i].name]['default'].visibleZoomLevel !== 'undefined' && this.STYLE[layers[i].name]['default'].visibleZoomLevel !== -1) {
                                visibleLevel = this.STYLE[layers[i].name]['default'].visibleZoomLevel;
                            }
                            if (zoomLevel >= visibleLevel) {
                                if (layers[i].request != null) { //&& layers[i].readyState != XMLHttpRequest.DONE
                                    /*
                                     * request.abort 주석했다 다시 주석 해제
                                     * cause : 브라우저 request pending 야기
                                     * 2015.07.02
                                     * by lispee
                                     */
                                    //layers[i].request.abort();
                                }
                                layers[i].request = OpenLayers.Request.POST(this.buildFeaturesInExtentRequest(layers[i], extent));
                            }
                        }
                    }
                }
                if (this.editorToolbar != null) {
                    if (this.editorToolbar.controls[1].snapping.active) {
                        citus.task(function() {
                            this.setSnaping(this.editor.opts.snaping.layerName, this.editor.opts.snaping.tolerance);
                        }, this);
                    }
                }
            };

            this.map.events.register("moveend", this, mapEventHandler);
            this.map.events.register("updatesize", this, mapEventHandler);



            // make mouse wheel zooming disabled
            /*
            var disableMouseWheel = function(map) {
                var navControls = map.getControlsByClass('OpenLayers.Control.Navigation');
                for(var i = 0; i < navControls.length; ++i) {
                    navControls[i].disableZoomWheel();
                }
            }(this.map);
            */

            this.map.setCenter(
                new OpenLayers.LonLat(this.lon, this.lat).transform(this.epsg4326, this.epsgBase),
                this.initialZoom
            );

            return this;
        };

        this.buildFeaturesInExtentRequest = function(layer, extent, loaded) {
            var splited = this.splitLayerName(layer.name);
            return {
                url: this.shapePrefix + 'areaQuery.json.do',
                data: OpenLayers.Util.getParameterString({
                    table: splited.name,
                    code: splited.code,
                    MINX: Math.floor(extent.left),
                    MINY: Math.floor(extent.bottom),
                    MAXX: Math.ceil(extent.right),
                    MAXY: Math.ceil(extent.top)
                }),
                scope: this,
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                callback: function(response) {
                    var msg = (response.status == 200) ? function(response) {
                        //var layer = this.map.getLayersByName(name)[0];
                        layer.destroyFeatures();
                        if (response.responseText) {
                            layer.addFeatures(
                                new OpenLayers.Format.GeoJSON({
                                    internalProjection: this.map.getProjection(),
                                    externalProjection: this.epsgProj
                                })
                                .read(response.responseText)
                            );
                        }
                        layer.request = null;
                        return 'success';
                    }.bind(this)(response) : 'fail';

                    if (typeof loaded !== 'undefined') {
                        loaded.apply(this, [{
                            message: msg
                        }]);
                    }

                    if (layer.pendingHighlight) {
                        var f = layer.pendingHighlight.feature;
                        this.selectFeatureByAttribute(f, layer.pendingHighlight.callback);
                        layer.pendingHighlight = null;
                    }
                }
            };
        };

        this.splitLayerName = function(name) {
            var splited = name.split('.', 2);
            return {
                name: splited[0],
                code: splited[1]
            };
        };

        this.joinLayerName = function(table, code) {
            return table + '.' + code;
        };

        // name = WTL_MANH_PS.SA100 or WTL_MANH_PS.SA991
        this.addLayer = function(name, loaded, styleCtx) {
            if (this.editor) {
                return this;
            }
            if (typeof styleCtx === "undefined") {
                styleCtx = this.styleContext;
            }

            var layer = new OpenLayers.Layer.Vector(name, {
                // http://docs.openlayers.org/library/feature_styling.html
                styleMap: new OpenLayers.StyleMap({
                    'default': new OpenLayers.Style(this.STYLE[name] ? this.STYLE[name]['default'] : {}, {
                        context: styleCtx['default']
                    }),
                    select: new OpenLayers.Style(this.STYLE[name] ? this.STYLE[name].select : {}, {
                        context: styleCtx.select
                    })
                })
            });
            layer.layerCode = name.slice('.', 1);
            layer.request = null; // ajax request in mapEventHandler()
            this.map.addLayer(layer);

            layer.setVisibility(true);

            citus.task(function() {
                this.map.events.triggerEvent("moveend");
            }.bind(this));

            return this;
        };

        this.removeLayer = function(name) {
            if (this.editor) {
                return this;
            }
            var layer = this.map.getLayersByName(name)[0];
            if (layer.request) {
                layer.request.abort();
                layer.request = null;
            }
            this.map.removeLayer(layer);

            return this;
        };

        this.baseLayer = function() {
            return (this.map.baseLayer.name == "VBASE") ? "vworldmap" : "localmap";
        };

        this.activeLayer = function(name) {
            var layers = this.map.getLayersBy("visibility", true);
            if (layers.length == 0) {
                throw {
                    message: 'no layer added to the map'
                };
            }
            var activeLayer = null;
            for (var i = 0, len = layers.length; i < len; i++) {
                if (layers[i].isBaseLayer === false) {
                    activeLayer = layers[i];
                }
            }

            if (typeof name === 'undefined') {
                return activeLayer.name;
            }
            if (this.editor) {
            	this.cancelAdding(function(fc, mode) {
                    return false;
                }, function() {});
                wfmsMap.tool_toggle();
                wfmsMap.map_tool_pan();       	
            	
                //return this;
            }
            var layer = this.map.getLayersByName(name)[0];
            this.map.removeLayer(layer);
            this.map.addLayer(layer);
            //this.map.raiseLayer(layer, this.map.layers.length);   // no effect

            if (this.navToolbar.highlightCtrl !== null) {
                this.map.removeControl(this.navToolbar.highlightCtrl);
            }
            if (this.navToolbar.selectCtrl !== null) {
                this.map.removeControl(this.navToolbar.selectCtrl);
            }

            this.navToolbar.highlightCtrl = new OpenLayers.Control.SelectFeature(layer, {
                multiple: false,
                hover: true,
                toggleKey: 'ctrlKey',
                multipleKey: 'shiftKey',
                highlightOnly: true,
                renderIntent: "temporary"
            });

            this.navToolbar.selectCtrl = new OpenLayers.Control.SelectFeature(layer, {
                clickout: true,
                toggle: false,
                toggleKey: 'ctrlKey', // ctrl key removes from selection
                multipleKey: 'shiftKey', // shift key adds to selection
                box: true,
                onSelect: function(f) {
                    (this.options.handlers.onSelect != null) ? this.options.handlers.onSelect.apply(this, arguments) : null;
                }.bind(this),
                onUnselect: function(f) {
                    (this.options.handlers.onUnselect !== null) ? this.options.handlers.onUnselect.apply(this, arguments) : null;
                }.bind(this)
            });

            this.map.addControl(this.navToolbar.highlightCtrl);
            this.map.addControl(this.navToolbar.selectCtrl);

            return this;
        };

        this.switchBaseLayer = function(layerIndx) {

            var layerName = '';
            switch (parseInt(layerIndx, 10)) {
                case 0:
                    layerName = 'geoserver';
                    break;
                case 1:
                    layerName = 'VBASE';
                    break;
            }

            this.map.baseLayer.setVisibility(false);
            this.map.setBaseLayer(this.map.getLayersByName(layerName)[0]);
            this.map.baseLayer.setVisibility(true);
        };

        this.getScale = function() {
            return this.map.getScale();
        };

        this.displayScale = function(scaleobj) {
            var scale = parseInt(this.getScale(), 10);
            scale = scale.toLocaleString();
            scaleobj.val(scale);
        };

        this.setScale = function(scale) {
            if (isNaN(scale)) return false;
            return this.map.zoomToScale(scale, true);
        };

        this.redraw = function() {
            var layers = this.map.getLayersBy("visibility", true);
            for (var i = 0, len = layers.length; i < len; i++) {
                layers[i].redraw();
            }
            return this;
        };

        this.setSelectionMode = function() {
            this.navToolbar.deactivateControls();
            this.navToolbar.activateSelectionControl();

            return this;
        };

        this.setNavPanMode = function() {
            this.navToolbar.deactivateControls();
            this.navToolbar.activateControl(this.navToolbar.controls[0]);

            return this;
        };

        this.setNavZoomInMode = function() {
            this.navToolbar.deactivateControls();
            this.navToolbar.activateControl(this.navToolbar.controls[1]);

            return this;
        };

        this.setNavZoomOutMode = function() {
            this.navToolbar.deactivateControls();
            this.navToolbar.activateControl(this.navToolbar.controls[2]);

            return this;
        };

        this.transectionMode = function(callback) {
            var layer = this.map.getLayersByName(this.activeLayer())[0];
            this.navToolbar.deactivateControls();
            this.map.addControl(new OpenLayers.Control.Transection(layer, {
                titile: 'Transection',
                autoActivate: true,
                result_callback: callback
            }));

            return this;

        };

        this.hideLayer = function(name) {
            if (this.editor) {
                return this;
            }
            this.setNavPanMode();
            var layers = this.map.getLayersByName(name);
            layers.length > 0 ? layers[0].setVisibility(false) : null;

            return this;
        };

        this.showLayer = function(name, loaded) {
            if (this.editor) {
                return this;
            }
            this.setNavPanMode();
            var layers = this.map.getLayersByName(name);
            layers.length > 0 ? layers[0].setVisibility(true) : this.addLayer(name, loaded);

            if (typeof loaded !== 'undefined') {
                citus.task(function() {
                    loaded.apply(this, arguments);
                }, this);
            }
            return this;
        };

        this.featureByZoomLevel = function(name, z) {
            if (typeof z === 'undefined') {
                return typeof this.STYLE[name]['default'].visibleZoomLevel === 'undefined' ? -1 : this.STYLE[name]['default'].visibleZoomLevel;
            }
            this.STYLE[name]['default'].visibleZoomLevel = z;
            this.map.events.triggerEvent("moveend");
            return z;
        };

        this.setLabel = function(name, attr, xoff, yoff) {
            this.STYLE[name]['default'].label = '${' + (attr == null ? 'label' : attr) + '}';
            var yOffset = 15;
            var xOffset = 0;

            if (typeof xoff !== 'undefined') {
                xOffset = xoff;
            }
            if (typeof yoff !== 'undefined') {
                yOffset = -yoff;
            }

            this.STYLE[name]['default'].labelXOffset = xOffset;
            this.STYLE[name]['default'].labelYOffset = yOffset;
            this.map.events.triggerEvent("moveend");
        };

        this.showFullMap = function() {
            this.setNavPanMode();
            if (this.options.basemap === 'localmap') {
                this.center(330374.24, 510509.96, this.initialZoom, "5186"); // lon, lat, zoom, epsg5186
                //this.map.zoomToMaxExtent();
            } else {
                this.map.setCenter(
                    new OpenLayers.LonLat(this.lon, this.lat).transform(this.epsg4326, this.epsgBase),
                    this.initialZoom
                );
            }

            return this;
        };

        this.center = function(lon, lat, zoom, epsg) {
            if (typeof lon === 'undefined') {
                var lonlat = this.map.getCenter().transform(this.epsgBase, this.epsg4326);
                return {
                    lon: lonlat.lon,
                    lat: lonlat.lat,
                    epsg: '4326',
                    zoom: this.map.getZoom()
                };
            } else {
                this.setNavPanMode();
                this.map.setCenter(
                    new OpenLayers.LonLat(lon, lat).transform(this["epsg" + ((typeof epsg === 'undefined') ? '4326' : epsg)], this.epsgBase),
                    zoom
                );
                return this;
            }
        };

        this.zoomTo = function(f) {
            if (typeof f !== "undefined" && typeof f.layer !== "undefined") {
                f.layer.pendingHighlight = {
                    feature: f,
                    layer: f.layer,
                    callback: function() {}
                };

                f.geometry.calculateBounds();
                this.map.zoomToExtent(f.geometry.bounds.clone().transform(this.epsgProj, this.epsgBase));
                //this.map.setCenter(f.geometry.getCentroid().x, f.geometry.getCentroid().y, this.map.getZoom());
            }
        };

        this.setMarker = function(lon, lat, zoom, epsg) {
            var layers = this.map.getLayersByName("MARKERS");
            var markerLayer = (layers.length > 0) ? layers[0] : new OpenLayers.Layer.Markers("MARKERS");


            markerLayer.addMarker(new OpenLayers.Marker(
                new OpenLayers.LonLat(lon, lat).transform(this["epsg" + (epsg || '4326')], this.epsgBase),
                new OpenLayers.Icon(this.webgisPrefix + 'images/marker.png',
                    new OpenLayers.Size(21, 32),
                    new OpenLayers.Pixel(-11, -32)
                )
            ));

            return this.center(lon, lat, zoom, epsg);
        };

        this.resetMarkers = function() {
            var markerLayer = this.map.getLayersByName("MARKERS")[0];
            markerLayer.clearMarkers();

            return this;
        };

        this.measureDist = function(callback) {
            this.navToolbar.deactivateControls();

            if (typeof callback === 'undefined') {
                callback = this.options.measures.dist;
            } else {
                this.options.measures.dist = callback;
            }
            this.navToolbar.activateControl(this.navToolbar.controls[3]);
            return this;
        };

        this.measureArea = function(callback) {
            this.navToolbar.deactivateControls();

            if (typeof callback === 'undefined') {
                callback = this.options.measures.area;
            } else {
                this.options.measures.area = callback;
            }
            this.navToolbar.activateControl(this.navToolbar.controls[4]);
            return this;
        };

        this.movePrevPosition = function() {
            this.setNavPanMode();
            this.history.previousTrigger();
            return this;
        };

        this.moveNextPosition = function() {
            this.setNavPanMode();
            this.history.nextTrigger();
            return this;
        };

        this.buildFindFeatureRequest = function(filter, callback) {
            var splited = this.splitLayerName(filter.layer);
            return {
                url: this.shapePrefix + 'FindFeature.json.do',
                data: OpenLayers.Util.getParameterString({
                    table: splited.name,
                    code: splited.code,
                    index: filter.index
                }),
                scope: this,
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                callback: function(req) {
                    var layer = this.map.getLayersByName(filter.layer)[0];
                    var e = (req.status == 200) ? function(req) {
                        if (req.responseText) {
                            var f = new OpenLayers.Format.GeoJSON().read(req.responseText)[0];
                            if (typeof f === "undefined") {
                                return {
                                    message: 'fail'
                                };
                            }
                            f.layer = layer;
                            f.centroid = f.geometry.getCentroid();
                            return {
                                message: 'success',
                                feature: f
                            };
                        }
                        return {
                            message: 'fail'
                        };
                    }.bind(this)(req) : {
                        message: 'fail'
                    };

                    if (typeof layer !== "undefined") layer.requestFindFeature = null;

                    if (typeof callback !== 'undefined') {
                        callback.apply(this, [e]);
                    }
                }
            };
        };

        this.selectFeatureByAttribute = function(ftr, callback) {
            var layer = ftr.layer;
            var len = layer.features.length;
            for (var i = 0; i < len; i++) {
                var f = layer.features[i];
                if (f.attributes['FTR_IDN'] == ftr.attributes['FTR_IDN'] && f.attributes['FTR_CDE'] == ftr.attributes['FTR_CDE']) {
                    this.navToolbar.selectCtrl.unselectAll();
                    this.navToolbar.selectCtrl.highlight(f);
                    citus.task(function() {
                        callback.apply(this, [f]);
                    }, this);
                    break;
                }
            }
        };

        /*
        this.findFeatures = function(filter)  {

            return this;
        };
        */

        this.findFeature = function(filter, callback) {
            if (this.editor) {
            	this.cancelAdding(function(fc, mode) {
                    return false;
                }, function() {});
                wfmsMap.tool_toggle();
                wfmsMap.map_tool_pan();
                /*citus.task(function() {
                    callback.apply(this, [{
                        message: 'fail'
                    }]);
                }, this);*/
            } 
            
            filter.layer.requestFindFeature = OpenLayers.Request.POST(this.buildFindFeatureRequest(
                filter,
                callback
            ));

            return this;
        };

        this.highlightFeature = function(f, callback) {
            if (this.editor) {
                citus.task(function() {
                    callback.apply(this, [{
                        message: 'fail'
                    }]);
                }, this);
            } else {
                var centroid = f.geometry.getCentroid();

                f.layer.pendingHighlight = {
                    feature: f,
                    layer: f.layer,
                    callback: callback
                };
                this.center(centroid.x, centroid.y, this.visibleZoomLevel, this.epsgCode + "");
            }

            return this;
        };

        this.unhighlightFeature = function(f) {
            if (this.editor) {
                citus.task(function() {
                    callback.apply(this, [{
                        message: 'fail'
                    }]);
                }, this);
            } else {
                this.navToolbar.selectCtrl.unhighlight(f);
            }

            return this;
        };

        this.transformCoordinate = function(x, y, srcCoord, dstCoord) {
            var srcPtr = new OpenLayers.Geometry.Point(x, y);
            var dstPtr = OpenLayers.Projection.transform(srcPtr, this['epsg' + srcCoord], this['epsg' + dstCoord]);
            return {
                x: dstPtr.x,
                y: dstPtr.y
            };
        };

        this.findIntersectedFeatures = function(fc, f) {
            var geometry = f.geometry;
            var relatives = [];
            for (var i = 0; i < fc.length; i++) {
                if (f.id !== fc[i].id && geometry.intersects(fc[i].geometry)) {
                    relatives.push(fc[i]);
                    fc[i].attributes.intersectionX = 0.1;
                    fc[i].attributes.intersectionY = 0.1;
                    fc[i].attributes.distance = 0.1;
                }
            }
            return relatives;
        };

        this.onFeatureModified = function(e) {
            if (e.feature && this.editor) {
                this.editor.featuresMod[e.feature.attributes['FTR_IDN']] = e.feature;
            }
        };

        this.onFeatureAdded = function(e) {
            if (e.feature && this.editor) {
                if (e.feature.state === OpenLayers.State.UPDATE && e.feature.reason === 'SPLIT') {
                    // merging or spliting
                    var splitingParam = this.editor.opts.splitingParam;
                    if (splitingParam.callback) {
                        citus.task(function() {
                            splitingParam.callback.apply(this, [e.feature, 'Mod']);
                        }, this);
                    }

                    this.editor.featuresMod[e.feature.attributes['FTR_IDN']] = e.feature;
                } else if (e.feature.state === OpenLayers.State.INSERT && e.feature.reason === 'SPLIT') {
                    // merging or spliting
                    var splitingParam = this.editor.opts.splitingParam;
                    if (splitingParam.callback) {
                        citus.task(function() {
                            splitingParam.callback.apply(this, [e.feature, 'Add']);
                        }, this);
                    }
                    var ftr_idx = (typeof e.feature.attributes['FTR_IDN'] != 'undefined') ? e.feature.attributes['FTR_IDN'] : -1;
                    e.feature.attributes['FTR_IDN'] = ftr_idx;
                    this.editor.featuresAdd[ftr_idx] = e.feature;
                } else if (e.feature.state === OpenLayers.State.UPDATE && e.feature.reason === 'MERGE') {
                    // merging or spliting
                    var mergingParam = this.editor.opts.mergingParam;
                    if (mergingParam.callback) {
                        citus.task(function() {
                            mergingParam.callback.apply(this, [e.feature, 'Mod']);
                        }, this);
                    }

                    this.editor.featuresMod[e.feature.attributes['FTR_IDN']] = e.feature;
                } else if (this.editor.opts.append) {
                    // adding
                    var addingParam = this.editor.opts.addingParam;

                    e.feature.attributes = addingParam.featureTemplate;
                    e.feature.data = addingParam.featureTemplate;
                    var centroid = e.feature.geometry.getCentroid().transform(this.epsgBase, this.epsgProj);
                    e.feature.centroid = {
                        x: centroid.x,
                        y: centroid.y,
                        epsg: this.epsgCode
                    };

                    if (!this.editor.featuresAdd[e.feature.attributes['FTR_IDN']]) {

                        // checking intersection
                        var relatives = this.findIntersectedFeatures(this.editor.targetLayer.features, e.feature);
                        if (this.FEATURE[this.editor.editLayer.name].type === "path" && relatives.length === 1) {
                            var feature = addingParam.feature = e.feature;
                            var wktFormat = new OpenLayers.Format.WKT();
                            var geo = wktFormat.write(relatives[0]),
                                cut = wktFormat.write(feature);

                            if (this.firstArcize) {
                                OpenLayers.Request.POST({
                                    url: this.shapePrefix + 'ArcizeGeometry.json.do',
                                    data: OpenLayers.Util.getParameterString({
                                        cut: cut,
                                        geo: geo,
                                        radius: addingParam.options.overlapRadius
                                    }),
                                    headers: {
                                        "Content-Type": "application/x-www-form-urlencoded"
                                    },
                                    callback: this.arcizeCallback,
                                    scope: this
                                });
                            } else {
                                this.firstArcize = true;
                            }
                        } else {
                            this.editor.featuresAdd[e.feature.attributes['FTR_IDN']] = e.feature;
                            citus.task(function() {
                                this.endEditingInternal.apply(this, [
                                    "Add",
                                    function(fc) {
                                        return true;
                                    }.bind(this),
                                    addingParam.callback.bind(this),
                                    addingParam.options.onStart.bind(this)
                                ]);
                            }, this);
                        }
                    }
                }
            }
        };

        this.toFeatures = function(multiPolygon) {
            if (multiPolygon === null || typeof(multiPolygon) !== 'object') {
                throw new Error('Parameter does not match expected type.');
            }
            var features = [];
            if (!(multiPolygon instanceof Array)) {
                multiPolygon = [multiPolygon];
            }
            for (var i = 0, li = multiPolygon.length; i < li; i++) {
                if (multiPolygon[i].geometry.CLASS_NAME === 'OpenLayers.Geometry.MultiPolygon' ||
                    multiPolygon[i].geometry.CLASS_NAME === 'OpenLayers.Geometry.Collection') {
                    for (var j = 0, lj = multiPolygon[i].geometry.components.length; j < lj; j++) {
                        features.push(new OpenLayers.Feature.Vector(
                            multiPolygon[i].geometry.components[j]
                        ));
                    }
                } else if (multiPolygon[i].geometry.CLASS_NAME === 'OpenLayers.Geometry.Polygon') {
                    features.push(new OpenLayers.Feature.Vector(multiPolygon[i].geometry));
                }
            }
            return features;
        };

        this.arcizeCallback = function(response) {
            if (response.status == 200) {
                this.firstArcize = false;
                var geoJSON = new OpenLayers.Format.GeoJSON();
                var geo = geoJSON.read(response.responseText);
                var nf = this.toFeatures(geo)[0];
                //callback 에러 by lispee
                if (this.editor !== null && this.editor.opts.addingParam && this.editor.opts.addingParam.feature) {
                    var addingParam = this.editor.opts.addingParam;
                    var oldf = addingParam.feature;

                    nf.data = oldf.data;
                    nf.attributes = oldf.attributes;
                    nf.centroid = oldf.centroid;

                    this.editor.targetLayer.removeFeatures(oldf);
                    this.editor.targetLayer.addFeatures(nf);

                    this.editor.featuresAdd[nf.attributes['FTR_IDN']] = nf;

                    citus.task(function() {
                        this.endEditingInternal.apply(this, [
                            "Add",
                            function(fc) {
                                return true;
                            }.bind(this),
                            addingParam.callback.bind(this),
                            addingParam.options.onStart.bind(this)
                        ]);
                    }, this);
                } else {
                    citus.task(function() {
                        this.endEditingInternal.apply(this, [
                            "Add",
                            function(fc) {
                                return false;
                            }.bind(this)
                        ]);
                    }, this);
                }
            } else {
                alert('상하월 작성중 에러가 발생했습니다.');
                this.cancelAdding(function(fc, mode) {
                    if (mode == 'Add') {
                        return false;
                    }
                }, function() {});
            }
        };

        this.onFeatureDeleted = function(e) {
            if (e.feature && this.editor) {
                this.editor.featuresDel[e.feature.attributes['FTR_IDN']] = e.feature;
            }
        };

        this.onFeaturesRemoved = function(e) {
            if (e.features && this.editor) {
                for (var i = 0; i < e.features.length; i++) {
                    var f = e.features[i];
                    if (f.state === OpenLayers.State.DELETE && f.reason === 'MERGE') {
                        if (typeof this.editor.featuresMod[e.features[i].attributes['FTR_IDN']] != 'undefined') {
                            delete this.editor.featuresMod[e.features[i].attributes['FTR_IDN']];
                        }

                        this.editor.featuresDel[e.features[i].attributes['FTR_IDN']] = e.features[i];
                    } else if (f.state === OpenLayers.State.DELETE && f.reason === 'SPLIT') {} else {
                        this.editor.featuresDel[e.features[i].attributes['FTR_IDN']] = e.features[i];
                    }
                }
            }
        };

        this.startEditing = function(layerName, options) {
            this.resetMarkers();

            this.navToolbar.deactivateControls();
            this.map.removeControl(this.navToolbar);

            var targetLayer = this.map.getLayersByName(layerName)[0];
            var editLayer = targetLayer;
            /*
             * 에러 수정
             * 20b5.03.27 by lispee
             */
            if (typeof this.FEATURE[layerName] === "undefined") return;

            var typeGeometry = this.FEATURE[layerName].type;

            var opts = {
                append: false,
                addingParam: {
                    options: {
                        overlapRadius: 1.0
                    },
                    featureTemplate: null,
                    callback: null
                },
                mergingParam: {
                    callback: null
                },
                splitingParam: {
                    callback: null
                },
                snaping: {
                    layerName: '',
                    tolerance: 10
                }
            };

            OpenLayers.Util.extend(opts.snaping, this.options.snaping);
            OpenLayers.Util.extend(opts, options);

            if (this.editor === null) {
                this.editor = new OpenLayers.Editor(this.map, {
                    activeControls: [
                        'CADTools', // 0
                        'SnappingSettings', // 1
                        'ImportFeature', // 2
                        'MergeFeature', // 3
                        'SplitFeature', // 4
                        'DeleteAllFeatures', // 5
                        'DrawHole', // 6
                        'Navigation', // 7
                        'TransformFeature', // 8
                        'CleanFeature', // 9
                        'DeleteFeature', // 10
                        'DragFeature', // 11
                        'SelectFeature', // 12
                        'ModifyFeature' // 13
                    ],
                    featureTypes: [typeGeometry], // 14
                    targetLayer: targetLayer,
                    editLayer: editLayer,
                    undoRedoActive: true,
                    oleSplitUrl: this.options.url.shape + 'SplitGeometry.json.do',
                    oleMergeUrl: this.options.url.shape + 'MergeGeometry.json.do',
                    tableName: layerName.slice('.', 0),
                    /*
                     * requestSplitComplete, requestMergeComplete 재정의
                     * 2015.02.06. updated by lispee start
                     */
                    styleMap: editLayer.styleMap,
                    requestSplitComplete: function(response) {
                        var json = JSON.parse(response.responseText);
                        if (typeof json.error !== 'undefined') {
                            alert(json.error);
                        }

                        var geo = this.geoJSON.read(response.responseText);
                        var f = this.editLayer.selectedFeatures[0];
                        var nfc = this.toFeatures(geo);

                        if (nfc.length >= 2) {
                            OpenLayers.Util.extend(nfc[0].attributes, f.attributes);
                            nfc[0].data = nfc[0].attributes;

                            f.state = OpenLayers.State.DELETE;
                            f.reason = 'SPLIT';
                            nfc[0].state = OpenLayers.State.UPDATE;
                            nfc[0].reason = 'SPLIT';

                            for (var i = 1; i < nfc.length; i++) {

                                OpenLayers.Util.extend(nfc[i].attributes, f.attributes);
                                nfc[i].data = nfc[i].attributes;
                                nfc[i].state = OpenLayers.State.INSERT;
                                nfc[i].reason = 'SPLIT';
                            }

                            this.editLayer.removeFeatures(f);
                            this.editLayer.addFeatures(nfc);
                            this.editLayer.events.triggerEvent('featureselected');
                        }
                    },
                    requestMergeComplete: function(response) {
                        var json = JSON.parse(response.responseText);
                        if (typeof json.error !== 'undefined') {
                            alert(json.error);
                        }

                        var geo = this.geoJSON.read(response.responseText);
                        var fc = this.editLayer.selectedFeatures;
                        var nfc = this.toFeatures(geo);

                        if (fc.length > 1 && nfc.length > 0) {
                            for (var i = 0; i < fc.length; i++) {
                                fc[i].state = OpenLayers.State.DELETE;
                                fc[i].reason = 'MERGE';
                            }

                            OpenLayers.Util.extend(nfc[0].attributes, fc[0].attributes);
                            nfc[0].data = nfc[0].attributes;

                            nfc[0].state = OpenLayers.State.UPDATE;
                            nfc[0].reason = 'MERGE';

                            this.editLayer.removeFeatures(fc);
                            this.editLayer.addFeatures([nfc[0]]);
                            this.editLayer.events.triggerEvent('featureselected');
                        }
                    }
                    /*
                     * 2015.02.06. updated by lispee end
                     */
                });
            }

            this.editor.opts = opts;

            this.editorToolbar = {};
            this.editorToolbar.controls = this.editor.editorPanel.controls;
            this.editorToolbar.deactivateControls = function() {
                var len = this.editorToolbar.controls.length;
                for (var i = 0; i < len; i++) {
                    this.editorToolbar.controls[i].deactivate();
                }
            }.bind(this);

            this.editor.featuresMod = {};
            this.editor.featuresDel = {};
            this.editor.featuresAdd = {};

            /*
             * startEditing시 등록되는 이벤트를 한번만 등록될수 있도록 수정
             * 20b5.03.27 by lispee
             */
            //this.editorToolbar.controls[14].events.register('featureadded', this, this.onFeatureAdded);
            if (!editLayer.events.listeners.hasOwnProperty('featureadded')) editLayer.events.register('featureadded', this, this.onFeatureAdded);
            if (!editLayer.events.listeners.hasOwnProperty('afterfeaturemodified')) editLayer.events.register('afterfeaturemodified', this, this.onFeatureModified);
            if (!editLayer.events.listeners.hasOwnProperty('featuredeleted')) editLayer.events.register('featuredeleted', this, this.onFeatureDeleted);
            if (!editLayer.events.listeners.hasOwnProperty('featuresremoved')) editLayer.events.register('featuresremoved', this, this.onFeaturesRemoved);
            if (!this.editorToolbar.controls[11].events.listeners.hasOwnProperty('dragcomplete')) this.editorToolbar.controls[11].events.register('dragcomplete', this, this.onFeatureModified);
            if (!this.editorToolbar.controls[13].layer.events.listeners.hasOwnProperty('vertexmodified')) this.editorToolbar.controls[13].layer.events.register('vertexmodified', this, this.onFeatureModified);

            var resetUpdateReq = function(req) {
                if (req != null) {
                    req.abort();
                }
            };
            resetUpdateReq(editLayer.updateMod);
            editLayer.updateMod = null;
            resetUpdateReq(editLayer.updateAdd);
            editLayer.updateAdd = null;
            resetUpdateReq(editLayer.updateDel);
            editLayer.updateDel = null;

            citus.task(function() {
                this.setSnaping(this.editor.opts.snaping.layerName, this.editor.opts.snaping.tolerance);
            }, this);

            this.editor.startEditMode();

            this.controlUndoRedo().register();

            return this;
        };

        this.updateGeometry = function(layer, mode, fc, callback, notify) {
            var geojson = new OpenLayers.Format.GeoJSON().write(fc);
            return {
                url: this.shapePrefix + mode + 'Geometry.json.do',
                data: OpenLayers.Util.getParameterString({
                    tableName: layer.name.slice('.', 0),
                    featureCollection: geojson
                }),
                scope: this,
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                callback: function(req) {
                    var msg = (req.status == 200) ? function(req) {
                        // do something
                        return 'success';
                    }.bind(this)(req) : 'fail';

                    if (typeof callback !== 'undefined') {
                        citus.task(function() {
                            callback.apply(this, [{
                                message: msg,
                                features: fc
                            }]);
                        }, this);
                    }
                    layer['update' + mode] = null;
                    //Split, Merge의 연장선. by lispee
                    if (typeof notify !== 'undefined') {
                        citus.task(function() {
                            notify.apply(this, arguments);
                        }.bind(this));
                    }
                }
            };
        };

        this.updateRequest = function(features, layer, mode, notify) {
            if (features.length > 0) {
                if (layer['update' + mode]) {
                    layer['update' + mode].abort();
                }
                for (var i = 0; i < features.length; i++) {
                    features[i].geometry.transform(this.epsgBase, this.epsgProj);
                }
                /*
                 * mode==Mod시  features의 특성에 따라 Mod, Add
                 * because of splitFeature, mergeFeature
                 * by lispee
                 */
                if (typeof features[0].reason !== "undefined" && (features[0].reason == "SPLIT" || features[0].reason == "MERGE")) {
                    for (var i = 0; i < features.length; i++) {
                        var feature = features[i];
                        var features2 = [];
                        features2.push(feature);
                        var mode2;
                        if (feature.state == "Insert") mode2 = "Add";
                        else if (feature.state == "Delete") mode2 = "Del";
                        else mode2 = mode;
                        var notify2;
                        var event2;
                        /*
                         * 임시로 마지막 notify와 event만 유효하도록 처리
                         * 향후 방안 마련해야 함.(비동기라 어느것이 마지막이 될 지 알 수 없읍)
                         * 차라리, Add, Mod, Del을 따로 던질게 아니라
                         * 복합해서 던지는 걸 만들 필요가 있음.
                         */
                        if (i == features.length - 1) {
                            notify2 = notify;
                            event2 = function() {
                                this.map.events.triggerEvent("moveend");
                            };
                        } else {
                            notify2 = undefined;
                            event2 = undefined;
                        }

                        layer['update' + mode2] = OpenLayers.Request.POST(
                            this.updateGeometry.apply(this, [layer, mode2, features2, notify2, event2])
                        );
                    }
                } else {
                    layer['update' + mode] = OpenLayers.Request.POST(
                        this.updateGeometry.apply(this, [layer, mode, features, notify,
                            function() {
                                this.map.events.triggerEvent("moveend");
                            }
                        ])
                    );
                }
            } else {
                layer['update' + mode] = OpenLayers.Request.POST(
                    this.updateGeometry.apply(this, [layer, mode, features, notify,
                        function() {
                            this.map.events.triggerEvent("moveend");
                        }
                    ])
                );
            }
        };

        this.getherFeaturesByMode = function(mode) {
            var features = [];
            for (var fid in this.editor['features' + mode]) {
                if (mode == 'Del') {
                    if (typeof this.editor.featuresMod[fid] == 'undefined') {
                        features.push(this.editor['features' + mode][fid]);
                    }
                } else {
                    features.push(this.editor['features' + mode][fid]);
                }
            }
            return features;
        };

        this.cancelAdding = function(confirm, notify) {
            return this.endEditingInternal("Add", confirm, notify);
        };

        this.endEditing = function(confirm, notify) {
            return this.endEditingInternal("Mod", confirm, notify);
        };

        this.endEditingInternal = function(mode, confirm, notify, notify_start) {

            // Editing 에러 수정. by lispee
            if (this.editor == null) return;
            this.controlUndoRedo().reset();

            this.editor.stopEditMode();

            var layer = this.editor.editLayer;
            /*
             * StartEditing시 register한 이벤트는 반드시 해제하고
             * property delete.
             * Edited by lispee. 2015.03.27
             */
            if (layer.events.listeners.hasOwnProperty('featureadded')) {
                layer.events.unregister('featureadded', this, this.onFeatureAdded);
                delete layer.events.listeners.featureadded;
            }
            if (layer.events.listeners.hasOwnProperty('afterfeaturemodified')) {
                layer.events.unregister('afterfeaturemodified', this, this.onFeatureModified);
                delete layer.events.listeners.afterfeaturemodified;
            }
            if (layer.events.listeners.hasOwnProperty('featuredeleted')) {
                layer.events.unregister('featuredeleted', this, this.onFeatureDeleted);
                delete layer.events.listeners.featuredeleted;
            }
            if (layer.events.listeners.hasOwnProperty('featuresremoved')) {
                layer.events.unregister('featuresremoved', this, this.onFeaturesRemoved);
                delete layer.events.listeners.featuresremoved;
            }
            if (this.editorToolbar.controls[11].events.listeners.hasOwnProperty('dragcomplete')) {
                this.editorToolbar.controls[11].events.unregister('dragcomplete', this, this.onFeatureModified);
                delete this.editorToolbar.controls[11].events.listeners.dragcomplete;
            }
            if (this.editorToolbar.controls[13].layer.events.listeners.hasOwnProperty('vertexmodified')) {
                this.editorToolbar.controls[13].layer.events.unregister('vertexmodified', this, this.onFeatureModified);
                delete this.editorToolbar.controls[13].layer.events.listeners.vertexmodified;
            }

            var features;
            // split, merge 연장선 . by lispee
            var featuresDel;
            var featuresMod;
            if (mode === 'Mod') {
                if (Object.keys(this.editor.featuresDel).length > 0) {
                    featuresDel = this.getherFeaturesByMode('Del');
                    /*if (featuresDel.length > 0) {
                        confirm.apply(this, [features, 'Del']);
                    }*/
                }

                if (Object.keys(this.editor.featuresAdd).length > 0) {
                    featuresMod = this.getherFeaturesByMode('Add');
                    /*confirm.apply(this, [features, 'Add']);*/
                }
            }

            features = this.getherFeaturesByMode(mode);
            if (typeof featuresDel != 'undefined') features = features.concat(featuresDel);
            if (typeof featuresMod != 'undefined') features = features.concat(featuresMod);
            if (confirm.apply(this, [features, mode]) === true) {
                if (typeof notify_start != 'undefined') {
                    notify_start.apply(this);
                }
                this.updateRequest.apply(this, [features, layer, mode, notify]);
            } else {
                citus.task(function() {
                    this.map.events.triggerEvent("moveend");
                }, this);

                if (console) {
                    console.log("the user cancels to update features.");
                }
            }

            delete this.editorToolbar;
            delete this.editor;

            this.editorToolbar = null;
            this.editor = null;

            this.map.addControl(this.navToolbar);
            this.navToolbar.activateDefaultControl();

            this.resetSnaping();

            return this;
        };

        this.addGeometryToDB = function(layerName, fc, callback, options) {
            var layer = this.map.getLayersByName(layerName)[0];
            this.updateRequest.apply(this, [fc, layer, 'Add', callback]);
        };

        this.delGeometryToDB = function(layerName, fc, callback, options) {
            var layer = this.map.getLayersByName(layerName)[0];
            this.updateRequest.apply(this, [fc, layer, 'Del', callback]);
        };

        /*
         * 관심영역 등록
         */
        this.addInterest = function(interstName, userId, callback) {
            	var extent = this.map.getExtent().transform(this.epsgBase, this.epsgProj);
                OpenLayers.Request.POST({
                    url: this.shapePrefix + 'AddInterest.json.do',
                    data: OpenLayers.Util.getParameterString({
                    	userId : userId,
                    	interstName: interstName,
                    	minx: extent.left,
                    	miny: extent.bottom,
                    	maxx: extent.right,
                    	maxy: extent.top
                    }),
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    callback: callback,
                    scope: this
                });

            return this;
        };
        
        /*
         * 관심영역 삭제
         */
        this.delInterest = function(int_idn, userId, callback) {
                OpenLayers.Request.POST({
                    url: this.shapePrefix + 'DelInterest.json.do',
                    data: OpenLayers.Util.getParameterString({
                    	userId : userId,
                    	intIdn: int_idn
                    }),
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    callback: callback,
                    scope: this
                });

            return this;
        };        
        
        /*
         * 관심영역 목록
         */
        this.listInterest = function(userId, callback) {
            OpenLayers.Request.POST({
                url: this.shapePrefix + 'ListInterest.json.do',
                data: OpenLayers.Util.getParameterString({
                	userId : userId
                }),
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                callback: callback,
                scope: this
            });

	        return this;
	    };        
        
        /*
         * 관심영역 보기
         */
        this.showInterest = function(min_x, min_y, max_x, max_y) {
        	var bound = new OpenLayers.Bounds(min_x, min_y, max_x, max_y);
        	this.map.zoomToExtent(bound.transform(this.epsgProj, this.epsgBase));

	        return this;
	    };    
	    
        this.addFeature = function(layerName, f, callback, options) {
            if (this.editor) {
                citus.task(function() {
                    callback.apply(this, [{
                        message: "not allowed in editing mode"
                    }]);
                }, this);
                return this;
            }

            if (typeof options == 'undefined') {
                options = {
                    onStart: function() {}
                };
            } else {
                if (typeof options.onStart == 'undefined') {
                    options.onStart = function() {};
                }
            }

            this.startEditing(layerName, {
                append: true,
                addingParam: {
                    options: options,
                    featureTemplate: f,
                    callback: callback
                }
            });

            this.editorToolbar.deactivateControls();
            this.editorToolbar.controls[14].activate();

            return this;
        };

        this.modifyFeature = function(callback, options) {
            this.editorToolbar.deactivateControls();
            var ctrlOffset = (this.editor.featureTypes[0] === 'point') ? 11 : 13;
            this.editorToolbar.controls[ctrlOffset].activate();

            if (typeof callback !== 'undefined') {
                citus.task(function() {
                    callback.apply(this, arguments);
                }, this);
            }

            return this;
        };

        this.selectFeature = function(callback) {
            // editing 에러 수정. by lispee
            if (this.editorToolbar !== null) {
                this.editorToolbar.deactivateControls();
                this.editorToolbar.controls[12].activate();
            }

            return this;
        };

        this.dragFeature = function(callback) {
            this.editorToolbar.deactivateControls();
            this.editorToolbar.controls[11].activate();

            return this;
        };

        this.moveFeature = function(x, y, callback) {
            args = [0, 0]; // not moved
            var len = this.editor.editLayer.selectedFeatures.length;
            if (len > 0) {
                for (var i = 0; i < len; i++) {
                    var feature = this.editor.editLayer.selectedFeatures[i];
                    feature.geometry.move(x, y);
                    feature.layer.drawFeature(feature);
                }
            }

            if (typeof callback !== 'undefined') {
                citus.task(function() {
                    callback.apply(this, args);
                }, this);
            }

            return this;
        };

        this.deleteFeature = function(layerName, attributes, callback) {
            if (this.editor) {
                citus.task(function() {
                    callback.apply(this, [{
                        message: "not allowed in editing mode"
                    }]);
                }, this);
                return this;
            }

            var feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0, 0), attributes);
            var layer = this.map.getLayersByName(layerName)[0];
            this.updateRequest.apply(this, [
                [feature], layer, "Del", callback
            ]);

            return this;
        };

        this.selectedFeatures = function() {
            return this.editor.editLayer.selectedFeatures;
        };

        this.addModified = function(f) {
            this.editor.featuresMod[f.attributes['FTR_IDN']] = f;

            return this;
        };

        this.cleanFeature = function(callback) {
            this.editorToolbar.deactivateControls();
            this.editorToolbar.controls[9].activate();

            return this;
        };

        this.transformFeature = function(callback) {
            this.editorToolbar.deactivateControls();
            this.editorToolbar.controls[8].activate();

            return this;
        };

        this.navigationMode = function(callback) {
            this.editorToolbar.deactivateControls();
            this.editorToolbar.controls[7].activate();

            return this;
        };

        this.mergeFeatures = function(callback) {
            // editing 에러 수정, by lispee
            if (this.editorToolbar !== null) {
                this.editorToolbar.deactivateControls();
                this.editorToolbar.controls[3].activate();
                this.editor.opts.mergingParam.callback = callback;
            }

            return this;

        };

        this.splitFeature = function(callback) {
            // editing 에러 수정, by lispee
            if (this.editorToolbar !== null) {
                this.editorToolbar.deactivateControls();
                this.editorToolbar.controls[4].activate();

                this.editor.opts.splitingParam.callback = callback;
            }

            return this;

        };

        this.setSnaping = function(layerName, tolerance) {

            var layer = this.map.getLayersByName(layerName)[0];
            var meter = parseInt(this.editorToolbar.controls[1].snapping.getGeoTolerance(tolerance, 1 / this.map.resolution), 10);

            this.editorToolbar.controls[1].snappingLayers = [layer];
            this.editorToolbar.controls[1].toleranceInput = {
                value: meter
            };

            this.editorToolbar.controls[1].changeSnapping();
            return this;

        };

        this.resetSnaping = function() {
            if (this.editorToolbar != null) {
                this.editorToolbar.controls[1].snapping.deactivate();
            }
            /*var meter = this.editorToolbar.controls[1].snapping.getGeoTolerance(tolerance, 1/this.map.resolution);
            this.editorToolbar.controls[1].snappingLayers = [];
            this.editorToolbar.controls[1].toleranceInput = {
                value: meter
            };

            this.editorToolbar.controls[1].changeSnapping();*/

            return this;
        };

        this.settingSnap = function(layerName, tolerance) {
            if (typeof layerName === 'undefined') {
                return this.options.snaping;
            }
            this.options.snaping = {
                layerName: layerName,
                tolerance: tolerance
            };

            return this;
        };

        this.panEditor = function() {

            return this;

        };

        this.controlUndoRedo = function() {
            var a = this.map.getControlsByClass('OpenLayers.Editor.Control.UndoRedo');
            return a[a.length - 1];
        };

        this.undo = function() {
            var controlUndoRedo = this.controlUndoRedo();
            if (controlUndoRedo) controlUndoRedo.undo();
        };

        this.redo = function() {
            var controlUndoRedo = this.controlUndoRedo();
            if (controlUndoRedo) controlUndoRedo.redo();
        };

        this.searchPoi = function() {
            if ($("#q") != null && $('#q').val().length >= 2) {
                $('#search_result').empty();
                var data = {
                    f: 'json',
                    pretty: 'true',
                    q: $('#q').val(),
                    category: $('#category').val(),
                    //category: 'Juso',
                    //category: 'Poi',
                    pageUnit: 100,
                    pageIndex: 1,
                    output: 'json',
                    apiKey: '4FAC4DA4-D9B1-3CE5-891C-CE3664473E79'
                };

                var jsonp = new OpenLayers.Protocol.Script();
                jsonp.createRequest('http://map.vworld.kr/search.do', data, this.searchPoiResult);
            } else {
                alert('검색어를 2자 이상 입력해 주세요.');
            }
        }

        this.clearPoi = function() {
            if ($("#q") != null) $('#q').val('');
            if ($("#search_result") != null) {
                $('#search_result').empty();
                $('#search_result').css('height', '0px');
            }
            webgis.resetMarkers();
        }

        this.searchPoiResult = function(data, textStatus, jqXHR) {
            if ($('#search_result') != null) {
                $('#search_result').css('height', '200px');
                for (var i = 0; i < data.LIST.length; i++) {
                    if (data.category == 'Poi') {
                        $('#search_result').append('<p><a href="#" class="olSearchResult" id="result_' + i + '" x="' + data.LIST[i].xpos + '" y="' + data.LIST[i].ypos + '">' + data.LIST[i].nameFull + '</a>: ' + data.LIST[i].njuso + '</p>');
                    } else if (data.category == 'Juso') {
                        $('#search_result').append('<p><a href="#" class="olSearchResult" id="result_' + i + '" x="' + data.LIST[i].xpos + '" y="' + data.LIST[i].ypos + '">' + data.LIST[i].JUSO + '</a> (' + data.LIST[i].ZIP_CL + ')</p>');
                    } else if (data.category == 'Jibun') {
                        $('#search_result').append('<p><a href="#" class="olSearchResult" id="result_' + i + '" x="' + data.LIST[i].xpos + '" y="' + data.LIST[i].ypos + '">' + data.LIST[i].JUSO + '</a></p>');
                    } else {
                        $('#search_result').append('<p>잘못된 요청입니다.</p>');
                    }

                }
                $('.olSearchResult').click(function(e) {
                    webgis.setMarker(parseFloat($(this).attr('x')), parseFloat($(this).attr('y')), 17, '4019');
                });
            }
        }

        window['$$WebGIS$$'] = this;

    }; // end of citus.WebGIS


    $WebGIS.prototype = $WebGIS_proto;

    $citus = function() {
        this.loadJS = function(src, callback) {
            var s = document.createElement('script');
            s.src = src;
            s.async = false;
            s.onreadystatechange = s.onload = function() {
                var state = s.readyState;
                if (!callback.done && (!state || /loaded|complete/.test(state))) {
                    callback.done = true;
                    callback();
                }
            };
            document.getElementsByTagName('head')[0].appendChild(s);
        };

        /*
         * Recursively extend properties of two objects
         */
        this.extend = function(dst, src) {
            for (var p in src) {
                try {
                    // Property in destination object set; update its value.
                    if (src[p].constructor == Object) {
                        dst[p] = this.extend(dst[p], src[p]);
                    } else {
                        dst[p] = src[p];
                    }
                } catch (e) {
                    // Property in destination object not set; create it and set its value.
                    if (dst) {
                        dst[p] = src[p];
                    }
                }
            }
            return dst;
        };

        this.task = function(func, obj) {
            if (typeof func == "function") {
                setTimeout(func.bind(obj), 0);
            }
        };

        if (typeof Array.contain === 'undefined') {
            Array.prototype.contain = function(o) {
                return this.indexOf(o) < 0 ? false : true;
            };
        }

        if (typeof String.slice === 'undefined') {
            String.prototype.slice = function(separator, ordinal) {
                return this.split(separator)[ordinal];
            };
        }

        this.WebGIS = $WebGIS;
        this.VERSION_NUMBER = "Release 0.96 dev";
    };

    window['citus'] = new $citus();
})();