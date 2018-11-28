var vworldUrl = "http://map.vworld.kr";
var vworldBaseMapUrl = "http://xdworld.vworld.kr:8080/2d";
var vworldApiKey = "4FAC4DA4-D9B1-3CE5-891C-CE3664473E79";
var vworldVers = {
    Base:"201310", 
    Hybrid:"201310", 
    Satellite:"201301"
};
var vworldUrls = {
    base: vworldBaseMapUrl + "/Base/" + vworldVers.Base, 
    hybrid: vworldBaseMapUrl + "/Hybrid/" + vworldVers.Hybrid, 
    raster: vworldBaseMapUrl + "/Satellite/" + vworldVers.Satellite, 
    apiCheck: vworldUrl + "/checkAPINum.do?key=" + vworldApiKey + "&type=TMS"
};

var vworldUrlsExt = { blankimage:vworldUrl + "/images/maps/no_service.gif" };

function VScript(src) {
    document.write('<'+'script src="'+src+'"'+' type="text/javascript"><'+'/script>');
}

(function() {
    if (!window.vworld) {
        window.vworld = {};
    }

    vworld.WaterMark = OpenLayers.Class(OpenLayers.Control, {
        autoActivate: true, 
        element: null, 
        dataSource: "VWORLD", 
        isSimple: true, 
        emptyString: null, 
        initialize: function(options) {
            OpenLayers.Control.prototype.initialize.apply(this, arguments);
        }, 
        destroy: function() {
            this.deactivate();
            OpenLayers.Control.prototype.destroy.apply(this, arguments);
        }, 
        activate: function() {
            if (OpenLayers.Control.prototype.activate.apply(this, arguments)) {
                this.redraw();
                return true;
            } 
            else {
                return false;
            }
        }, 
        deactivate: function() {
            if (OpenLayers.Control.prototype.deactivate.apply(this, arguments)) {
                this.reset();
                return true;
            } 
            else {
                return false;
            }
        }, 
        draw:function() {
          OpenLayers.Control.prototype.draw.apply(this, arguments);
          if (!this.element) {
              this.div.style.left = "10px";
              this.div.style.bottom = "6px";
              this.element = this.div;
          }
          return this.div;
        }, 
        redraw: function(evt) {
            if (evt == null) {
                this.reset();
            }
            var newHtml = this.formatOutput();
            if (newHtml != this.element.innerHTML) {
                this.element.innerHTML = newHtml;
            }
        }, 
        reset: function(evt) {
            if (this.element != null) {
                this.element.innerHTML = "";
            }
        }, 
        formatOutput: function() {
            this.element.style.color = "#111";
            if (this.isSimple) {
                return "<img src='" + vworldUrl + "/images/maps/logo_openplatform_simple.png'>";
            } 
            else {
                return "<img src='" + vworldUrl + "/images/maps/logo_openplatform.png'>";
            }
        }, 
        CLASS_NAME:"vworld.WaterMark"
    });

    vworld.Layers = OpenLayers.Class(OpenLayers.Layer.Grid, {
        name:"VWORLD Layer", 
        tileOrigin: null, 
        min_level: 6, 
        max_level: 18, 
        //attribution: "Data by <a href='http://map.vworld.kr/'>VWORLD MAP</a>", 
        sphericalMercator: true, 
        wrapDateLine: true, 
        visibleCount: 1, 
        isBaseLayer: true, 
        visibility: true, 
        opacity: 1, 
        type: "png", 
        transitionEffect: "resize", 
        tileSize: new OpenLayers.Size(256, 256), 
        addedwatermark: false, 
        watermark: null, 
        initBounds: new OpenLayers.Bounds(120.9375, 31.95216223802497, 132.1875, 43.068), 
        initialize: function(name, url, options) {
            var newArguments = [];
            newArguments.push(name !== undefined ? name : this.name, url, {}, this.getOptions);
            OpenLayers.Layer.Grid.prototype.initialize.apply(this, newArguments);
        }, 
        destroy:function() {
            OpenLayers.Layer.Grid.prototype.destroy.apply(this, arguments);
        }, 
        clone:function(obj) {
            if (obj == null) {
                //obj = new VBase(this.name, this.url, this.getOptions());
            }
            obj = OpenLayers.Layer.Grid.prototype.clone.apply(this, [obj]);
            return obj;
        },
        getURL:function(bounds) {
            bounds = this.adjustBounds(bounds);
            var res = this.map.getResolution();
            var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
            var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
            var z = this.map.getZoom();

            if (z > this.max_level) {
                return null;
            }

            var limit = Math.pow(2, z);
            if (y < 0 || y >= limit) {
                return vworldUrlsExt.blankimage;
            } 
            else {
                x = (x % limit + limit) % limit;
                if (this.mapBounds.intersectsBounds(bounds)) {
                    if (z >= this.min_level && z <= this.max_level) {
                        var path = "/" + z + "/" + x + "/" + y + "." + this.type
                    } 
                    else {
                        if (z > this.max_level) {
                            var n = z - this.max_level;
                            var z2 = z - n;
                            var nsize = 256 * Math.pow(2, n);
                            var x = Math.round((bounds.left - this.maxExtent.left) / (res * nsize));
                            var y = Math.round((this.maxExtent.top - bounds.top) / (res * nsize));
                            x = (x % limit + limit) % limit;
                            var path = "/" + z2 + "/" + x + "/" + y + "." + this.type;
                        } 
                        else {
                            return vworldUrlsExt.blankimage;
                        }
                    }
                } 
                else {
                    return vworldUrlsExt.blankimage;
                }
                var url = this.url;
                if (url instanceof Array) {
                    url = this.selectUrl(path, url);
                }
                return url + path;
            }
        }, 
        addTile:function(bounds, position) {
            return new OpenLayers.Tile.Image(this, position, bounds, null, this.tileSize);
        }, 
        onZoomChanged:function(e) {
            var x = this.map.getZoom();
            if (x > this.max_level) {
                var n = x - this.max_level;
                var nsize = 256 * Math.pow(2, n);
                this.clearGrid();
                this.setTileSize(new OpenLayers.Size(nsize, nsize));
                this.redraw();
            } 
            else {
                if (this.tileSize == null) {
                    return;
                }
                if (this.tileSize.w != 256) {
                    this.clearGrid();
                    this.setTileSize(new OpenLayers.Size(256, 256));
                    this.redraw();
                }
            }
        }, 
        setMap:function(map) {
            OpenLayers.Layer.Grid.prototype.setMap.apply(this, arguments);
            this.mapBounds = this.initBounds.clone();
            this.mapBounds.transform(new OpenLayers.Projection("EPSG:4326"), map.projection);
            if (!this.tileOrigin) {
                this.tileOrigin = new OpenLayers.LonLat(this.map.maxExtent.left, this.map.maxExtent.top);
            }
        }, 
        setWaterMark:function() {
            if (vworld.Layers.prototype.addedwatermark) {
                return;
            }
            if (vworld.Layers.prototype.watermark == null) {
                vworld.Layers.prototype.watermark = new vworld.WaterMark;
                if (!vworld.Layers.prototype.addedwatermark) {
                    this.map.addControl(vworld.Layers.prototype.watermark);
                }
                vworld.Layers.prototype.addedwatermark = true;
            }
        }, 
        autoWaterMark:function(evt) {
            if (this != null && evt != null) {
                vworld.Layers.prototype.visibleCount += this.visibility ? 1 : -1;
            } 
            else {
                vworld.Layers.prototype.visibleCount -= 1;
            }
            if (this.map == null || (vworld == null || vworld.Layers == null)) {
                return;
            }
            try {
                if (vworld.Layers.prototype.visibleCount > 0) {
                    var controls = this.map.getControlsByClass("vworld.WaterMark");
                    if (controls != null) {
                        for (var i = 0;i < controls.length;i++) {
                            controls[i].activate();
                        }
                    }
                } 
                else {
                    var controls = this.map.getControlsByClass("vworld.WaterMark");
                    if (controls != null) {
                        for (var i = 0;i < controls.length;i++) {
                            controls[i].deactivate();
                        }
                    }
                }
            } 
            catch (e) {
            }
        }, 
        afterAdd:function() {
            vworld.Layers.prototype.visibleCount += this.visibility ? 1 : -1;
            //this.setWaterMark();
            this.map.events.unregister("moveend", this, this.onZoomChanged);
            this.map.events.register("moveend", this, this.onZoomChanged);
            this.events.unregister("visibilitychanged", this, this.autoWaterMark);
            this.events.register("visibilitychanged", this, this.autoWaterMark);
        }, 
        removeMap:function(map) {
            this.autoWaterMark(null);
            this.map.events.unregister("moveend", this, this.onZoomChanged);
            this.events.unregister("visibilitychanged", this, this.autoWaterMark);
            OpenLayers.Layer.Grid.prototype.removeMap.apply(this, arguments);
        }, 
        CLASS_NAME:"vworld.Layers"
    });
      
    vworld.Layers.Base = OpenLayers.Class(vworld.Layers, {
      name:"VWORLD BaseMap", 
      initialize:function(name, options) {
          if (options != undefined) {
              this.addOptions(options);
          }
          this.name = name !== undefined ? name : this.name;
          var newArguments = [];
          newArguments.push(this.name, this.url, {}, this.getOptions);
          OpenLayers.Layer.Grid.prototype.initialize.apply(this, newArguments);
          this.url = vworldUrls.base;
      }, 
      CLASS_NAME:"vworld.Layers.Base"
    });

    vworld.Layers.Satellite = OpenLayers.Class(vworld.Layers, {
      name:"VWORLD Satellite", 
      initialize:function(name, options) {
          if (options != undefined) {
              this.addOptions(options);
          }
          this.name = name !== undefined ? name : this.name;
          this.type = "jpeg";
          var newArguments = [];
          newArguments.push(this.name, this.url, {}, this.getOptions);
          OpenLayers.Layer.Grid.prototype.initialize.apply(this, newArguments);
          this.url = vworldUrls.raster;
      }, 
      CLASS_NAME:"vworld.Layers.Satellite"
    });

    vworld.Layers.Hybrid = OpenLayers.Class(vworld.Layers, {
      name:"VWORLD Hybrid", 
      initialize:function(name, options) {
          this.isBaseLayer = false;
          if (options != undefined) {
              this.addOptions(options);
          }
          this.name = name !== undefined ? name : this.name;
          var newArguments = [];
          newArguments.push(this.name, this.url, {}, this.getOptions);
          OpenLayers.Layer.Grid.prototype.initialize.apply(this, newArguments);
          this.url = vworldUrls.hybrid;
      }, 
      CLASS_NAME:"vworld.Layers.Hybrid"
    });

    //VScript(vworldUrls.apiCheck);
})();