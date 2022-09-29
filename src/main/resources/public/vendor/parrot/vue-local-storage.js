!function(e,t){"object"==typeof exports&&"undefined"!=typeof module?module.exports=t():"function"==typeof define&&define.amd?define(t):e.VueLocalStorage=t()}(this,function(){"use strict";var e=function(){this._properties={},this._namespace="",this._isSupported=!0},t={namespace:{}};t.namespace.get=function(){return this._namespace},t.namespace.set=function(e){this._namespace=e?e+".":""},e.prototype._getLsKey=function(e){return""+this._namespace+e},e.prototype._lsSet=function(e,t,r){var o=this._getLsKey(e),n=r&&[Array,Object].includes(r)?JSON.stringify(t):t;window.localStorage.setItem(o,n)},e.prototype._lsGet=function(e){var t=this._getLsKey(e);return window.localStorage[t]},e.prototype.get=function(e,t,r){var o=this;if(void 0===t&&(t=null),void 0===r&&(r=String),!this._isSupported)return null;if(this._lsGet(e)){var n=r;for(var s in o._properties)if(s===e){n=o._properties[s].type;break}return this._process(n,this._lsGet(e))}return null!==t?t:null},e.prototype.set=function(e,t){var r=this;if(!this._isSupported)return null;for(var o in r._properties){var n=r._properties[o].type;if(o===e)return r._lsSet(e,t,n),t}return this._lsSet(e,t),t},e.prototype.remove=function(e){return this._isSupported?window.localStorage.removeItem(e):null},e.prototype.addProperty=function(e,t,r){void 0===r&&(r=void 0),t=t||String,this._properties[e]={type:t},this._lsGet(e)||null===r||this._lsSet(e,r,t)},e.prototype._process=function(e,t){switch(e){case Boolean:return"true"===t;case Number:return parseFloat(t);case Array:try{var r=JSON.parse(t);return Array.isArray(r)?r:[]}catch(e){return[]}case Object:try{return JSON.parse(t)}catch(e){return{}}default:return t}},Object.defineProperties(e.prototype,t);var r=new e;return{install:function(e,t){if(void 0===t&&(t={}),"undefined"==typeof process||!(process.server||process.SERVER_BUILD||process.env&&"server"===process.env.VUE_ENV)){var o=!0;try{var n="__vue-localstorage-test__";window.localStorage.setItem(n,n),window.localStorage.removeItem(n)}catch(e){o=!1,r._isSupported=!1,console.error("Local storage is not supported")}var s=t.name||"localStorage",i=t.bind;t.namespace&&(r.namespace=t.namespace),e.mixin({beforeCreate:function(){var t=this;o&&this.$options[s]&&Object.keys(this.$options[s]).forEach(function(o){var n=t.$options[s][o],a=[n.type,n.default],p=a[0],c=a[1];if(r.addProperty(o,p,c),Object.getOwnPropertyDescriptor(r,o))e.config.silent||console.log(o+": is already defined and will be reused");else{var u={get:function(){return e.localStorage.get(o,c)},set:function(t){return e.localStorage.set(o,t)},configurable:!0};Object.defineProperty(r,o,u),e.util.defineReactive(r,o,c)}(i||n.bind)&&!1!==n.bind&&(t.$options.computed=t.$options.computed||{},t.$options.computed[o]||(t.$options.computed[o]={get:function(){return e.localStorage[o]},set:function(t){e.localStorage[o]=t}}))})}}),e[s]=r,e.prototype["$"+s]=r}}}});