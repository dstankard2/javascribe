function _ins(parent,elt,prev) {
for(var i=0;i<parent.childNodes.length;i++) {
var done = true;
var n = parent.childNodes[i];
for(var i2=0;i2<prev.length;i2++) {
if (n._elt==prev[i2]) {
done = false;
break;
} // if
} // for
if (done) {
parent.insertBefore(elt,n);
return;
}
}// for
parent.appendChild(elt);

}
function _invokeRem(elt) {
if (elt.$$remove && elt.$$remove.length) {
for(var i=0;i<elt.$$remove.length;i++) {
elt.$$remove[i]();
}/* for */
delete elt.$$remove
} /* if */

}
function _rem(parent,toRemove) {
if (!toRemove) {
for(var i=0;i<parent.childNodes.length;i++) {
var node = parent.childNodes[i];
_rem(node);
} // for
_invokeRem(parent);
} // If
else {
for(var _i=0;_i<parent.childNodes.length;_i++) {
if (parent.childNodes[_i]._elt==toRemove) {
var node = parent.childNodes[_i];
_rem(node);
parent.removeChild(node);_i--;
} // If
} // for
} // Else

}
function TemplateSet_widgets_itemListing(selected,itemList,selectCallback) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget listing';
(function() {
var _e1;
_e1 = _d.createElement('ul');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.className = 'listing';
_e0.appendChild(_e1);
(function() {
var _e2;
var _lf0 = function(item){
_e2 = _d.createElement('li');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = ''+((function(){try{return item==selected ? 'selected': ''!=undefined?item==selected ? 'selected': '':'';}catch(_e){return '';}})())+' '+((function(){try{return item.state!=undefined?item.state:'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return item.name!=undefined?item.name:'';}catch(_e){return '';}})())+'');
_e2.appendChild(_t0);
}catch(_err){}
})();
_e2.addEventListener('click', function($event) {
$event.stopPropagation();
selectCallback(item);
});
}
try {
for(var _i0=0;_i0<itemList.length;_i0++) {
var item = itemList[_i0];
_lf0(item);
}
}catch(_err){}
})();
})();
return _e0;

}
function TemplateSet_widgets_attributeListing(selected,attributes,selectCallback) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget attribute-listing';
(function() {
var l;
(function() {
try{
l = [];
 for(const prop in attributes) {
 l.push(attributes[prop]);
 }
}catch(_e){
console.error(_e);
}
})();
var _e1;
_e1 = _d.createElement('ul');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.className = 'listing';
_e0.appendChild(_e1);
(function() {
var _e2;
var _lf0 = function(attr){
_e2 = _d.createElement('li');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = ''+((function(){try{return attr==selected ? 'selected': ''!=undefined?attr==selected ? 'selected': '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return attr.name!=undefined?attr.name:'';}catch(_e){return '';}})())+'');
_e2.appendChild(_t0);
}catch(_err){}
})();
_e2.addEventListener('click', function($event) {
$event.stopPropagation();
selectCallback(attr);
});
}
try {
for(var _i0=0;_i0<l.length;_i0++) {
var attr = l[_i0];
_lf0(attr);
}
}catch(_err){}
})();
})();
return _e0;

}
function TemplateSet_widgets_appName() {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget app-name';
(function() {
try {
var _t0 = _d.createTextNode('App Name');
_e0.appendChild(_t0);
}catch(_err){}
})();
return _e0;

}
function TemplateSet_widgets_attributeDetail(attribute) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget item-detail';
(function() {
var _e1;
_e1 = _d.createElement('table');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.border = '1';
_e1.className = 'info-container';
_e0.appendChild(_e1);
(function() {
var _e2;
_e2 = _d.createElement('tr');
_e2._elt = '_e2';
_e2.$$remove = [];
_e1.appendChild(_e2);
(function() {
var _e3;
_e3 = _d.createElement('td');
_e3._elt = '_e3';
_e3.$$remove = [];
_e2.appendChild(_e3);
(function() {
var _e4;
_e4 = _d.createElement('span');
_e4._elt = '_e4';
_e4.$$remove = [];
_e4.className = 'key';
_e3.appendChild(_e4);
(function() {
try {
var _t0 = _d.createTextNode('System Attribute');
_e4.appendChild(_t0);
}catch(_err){}
})();
})();
var _e4;
_e4 = _d.createElement('td');
_e4._elt = '_e4';
_e4.$$remove = [];
_e2.appendChild(_e4);
(function() {
var _e5;
_e5 = _d.createElement('div');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'value spaced';
_e4.appendChild(_e5);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return attribute.name!=undefined?attribute.name:'';}catch(_e){return '';}})())+'');
_e5.appendChild(_t0);
}catch(_err){}
})();
})();
})();
var _e3;
_e3 = _d.createElement('tr');
_e3._elt = '_e3';
_e3.$$remove = [];
_e1.appendChild(_e3);
(function() {
var _e4;
_e4 = _d.createElement('td');
_e4._elt = '_e4';
_e4.$$remove = [];
_e3.appendChild(_e4);
(function() {
var _e5;
_e5 = _d.createElement('span');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'key';
_e4.appendChild(_e5);
(function() {
try {
var _t0 = _d.createTextNode('Type');
_e5.appendChild(_t0);
}catch(_err){}
})();
})();
var _e5;
_e5 = _d.createElement('td');
_e5._elt = '_e5';
_e5.$$remove = [];
_e3.appendChild(_e5);
(function() {
var _e6;
_e6 = _d.createElement('div');
_e6._elt = '_e6';
_e6.$$remove = [];
_e6.className = 'value spaced';
_e5.appendChild(_e6);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return attribute.type!=undefined?attribute.type:'';}catch(_e){return '';}})())+'');
_e6.appendChild(_t0);
}catch(_err){}
})();
})();
})();
var _e4;
_e4 = _d.createElement('tr');
_e4._elt = '_e4';
_e4.$$remove = [];
_e1.appendChild(_e4);
(function() {
var _e5;
_e5 = _d.createElement('td');
_e5._elt = '_e5';
_e5.$$remove = [];
_e4.appendChild(_e5);
(function() {
var _e6;
_e6 = _d.createElement('span');
_e6._elt = '_e6';
_e6.$$remove = [];
_e6.className = 'key';
_e5.appendChild(_e6);
(function() {
try {
var _t0 = _d.createTextNode('Originators');
_e6.appendChild(_t0);
}catch(_err){}
})();
})();
var _e6;
_e6 = _d.createElement('td');
_e6._elt = '_e6';
_e6.$$remove = [];
_e4.appendChild(_e6);
(function() {
var _e7;
var _lf0 = function(o){
_e7 = _d.createElement('div');
_e7._elt = '_e7';
_e7.$$remove = [];
_e7.className = 'value spaced';
_e6.appendChild(_e7);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return window.getItemName(o)!=undefined?window.getItemName(o):'';}catch(_e){return '';}})())+'');
_e7.appendChild(_t0);
}catch(_err){}
})();
}
try {
for(var _i0=0;_i0<attribute.originators.length;_i0++) {
var o = attribute.originators[_i0];
_lf0(o);
}
}catch(_err){}
})();
})();
var _e5;
_e5 = _d.createElement('tr');
_e5._elt = '_e5';
_e5.$$remove = [];
_e1.appendChild(_e5);
(function() {
var _e6;
_e6 = _d.createElement('td');
_e6._elt = '_e6';
_e6.$$remove = [];
_e5.appendChild(_e6);
(function() {
var _e7;
_e7 = _d.createElement('span');
_e7._elt = '_e7';
_e7.$$remove = [];
_e7.className = 'key';
_e6.appendChild(_e7);
(function() {
try {
var _t0 = _d.createTextNode('Dependants');
_e7.appendChild(_t0);
}catch(_err){}
})();
})();
var _e7;
_e7 = _d.createElement('td');
_e7._elt = '_e7';
_e7.$$remove = [];
_e5.appendChild(_e7);
(function() {
var _e8;
var _lf0 = function(o){
_e8 = _d.createElement('div');
_e8._elt = '_e8';
_e8.$$remove = [];
_e8.className = 'value spaced';
_e7.appendChild(_e8);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return window.getItemName(o)!=undefined?window.getItemName(o):'';}catch(_e){return '';}})())+'');
_e8.appendChild(_t0);
}catch(_err){}
})();
}
try {
for(var _i0=0;_i0<attribute.dependants.length;_i0++) {
var o = attribute.dependants[_i0];
_lf0(o);
}
}catch(_err){}
})();
})();
})();
})();
return _e0;

}
function TemplateSet_widgets_typeListing(selected,typeList,selectCallback) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget type-listing';
(function() {
})();
return _e0;

}
function TemplateSet_widgets_fileDetail(file) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget file-detail';
(function() {
var _e1;
_e1 = _d.createElement('table');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.border = '1';
_e1.className = 'info-container';
_e0.appendChild(_e1);
(function() {
var _e2;
_e2 = _d.createElement('tr');
_e2._elt = '_e2';
_e2.$$remove = [];
_e1.appendChild(_e2);
(function() {
var _e3;
_e3 = _d.createElement('td');
_e3._elt = '_e3';
_e3.$$remove = [];
_e2.appendChild(_e3);
(function() {
var _e4;
_e4 = _d.createElement('span');
_e4._elt = '_e4';
_e4.$$remove = [];
_e4.className = 'key';
_e3.appendChild(_e4);
(function() {
try {
var _t0 = _d.createTextNode('Path');
_e4.appendChild(_t0);
}catch(_err){}
})();
})();
var _e4;
_e4 = _d.createElement('td');
_e4._elt = '_e4';
_e4.$$remove = [];
_e2.appendChild(_e4);
(function() {
var _e5;
_e5 = _d.createElement('div');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'value spaced';
_e4.appendChild(_e5);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return file.path!=undefined?file.path:'';}catch(_e){return '';}})())+'');
_e5.appendChild(_t0);
}catch(_err){}
})();
})();
})();
var _e3;
_e3 = _d.createElement('tr');
_e3._elt = '_e3';
_e3.$$remove = [];
_e1.appendChild(_e3);
(function() {
var _e4;
_e4 = _d.createElement('td');
_e4._elt = '_e4';
_e4.$$remove = [];
_e3.appendChild(_e4);
(function() {
var _e5;
_e5 = _d.createElement('span');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'key';
_e4.appendChild(_e5);
(function() {
try {
var _t0 = _d.createTextNode('Originators');
_e5.appendChild(_t0);
}catch(_err){}
})();
})();
var _e5;
_e5 = _d.createElement('td');
_e5._elt = '_e5';
_e5.$$remove = [];
_e3.appendChild(_e5);
(function() {
var _e6;
var _lf0 = function(o){
_e6 = _d.createElement('div');
_e6._elt = '_e6';
_e6.$$remove = [];
_e6.className = 'value spaced';
_e5.appendChild(_e6);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return window.getItemName(o)!=undefined?window.getItemName(o):'';}catch(_e){return '';}})())+'');
_e6.appendChild(_t0);
}catch(_err){}
})();
}
try {
for(var _i0=0;_i0<file.originators.length;_i0++) {
var o = file.originators[_i0];
_lf0(o);
}
}catch(_err){}
})();
})();
})();
})();
return _e0;

}
function TemplateSet_widgets_itemDetail(item) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget item-detail';
(function() {
var _e1;
_e1 = _d.createElement('fieldset');
_e1._elt = '_e1';
_e1.$$remove = [];
_e0.appendChild(_e1);
(function() {
var _e2;
_e2 = _d.createElement('legend');
_e2._elt = '_e2';
_e2.$$remove = [];
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode('Item '+((function(){try{return item.id!=undefined?item.id:'';}catch(_e){return '';}})())+'');
_e2.appendChild(_t0);
}catch(_err){}
})();
var _e3;
_e3 = _d.createElement('div');
_e3._elt = '_e3';
_e3.$$remove = [];
_e1.appendChild(_e3);
(function() {
try {
var _t0 = _d.createTextNode('Name: '+((function(){try{return item.name!=undefined?item.name:'';}catch(_e){return '';}})())+'');
_e3.appendChild(_t0);
}catch(_err){}
})();
var _e4;
var _b0 = false;
try {
_b0 = (item.originatorId);
} catch(_err) { }
if (_b0) {
_e4 = _d.createElement('div');
_e4._elt = '_e4';
_e4.$$remove = [];
_e1.appendChild(_e4);
(function() {
try {
var _t0 = _d.createTextNode('Originator: '+((function(){try{return window.getItemName(item.originatorId)!=undefined?window.getItemName(item.originatorId):'';}catch(_e){return '';}})())+'');
_e4.appendChild(_t0);
}catch(_err){}
})();
}
var _e5;
var _b1 = false;
try {
_b1 = (item.logs.length);
} catch(_err) { }
if (_b1) {
_e5 = _d.createElement('div');
_e5._elt = '_e5';
_e5.$$remove = [];
_e1.appendChild(_e5);
(function() {
var _e6;
_e6 = _d.createElement('div');
_e6._elt = '_e6';
_e6.$$remove = [];
_e5.appendChild(_e6);
(function() {
try {
var _t0 = _d.createTextNode('Log Messages');
_e6.appendChild(_t0);
}catch(_err){}
})();
var _e7;
_e7 = _d.createElement('ul');
_e7._elt = '_e7';
_e7.$$remove = [];
_e7.className = 'log-messages';
_e5.appendChild(_e7);
(function() {
var _e8;
var _lf0 = function(msg){
_e8 = _d.createElement('li');
_e8._elt = '_e8';
_e8.$$remove = [];
_e8.className = ''+((function(){try{return msg.level!=undefined?msg.level:'';}catch(_e){return '';}})())+'';
_e7.appendChild(_e8);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return msg.message!=undefined?msg.message:'';}catch(_e){return '';}})())+'');
_e8.appendChild(_t0);
}catch(_err){}
})();
}
try {
for(var _i0=0;_i0<item.logs.length;_i0++) {
var msg = item.logs[_i0];
_lf0(msg);
}
}catch(_err){}
})();
})();
}
})();
})();
return _e0;

}
function TemplateSet_widgets_fileListing(selected,files,selectCallback) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
_e0.className = 'widget file-listing';
(function() {
var l;
(function() {
try{
l = [];
 for(const prop in files) {
 l.push(files[prop]);
 }
}catch(_e){
console.error(_e);
}
})();
var _e1;
_e1 = _d.createElement('ul');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.className = 'listing';
_e0.appendChild(_e1);
(function() {
var _e2;
var _lf0 = function(file){
_e2 = _d.createElement('li');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = ''+((function(){try{return file==selected ? 'selected': ''!=undefined?file==selected ? 'selected': '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return file.path!=undefined?file.path:'';}catch(_e){return '';}})())+'');
_e2.appendChild(_t0);
}catch(_err){}
})();
_e2.addEventListener('click', function($event) {
$event.stopPropagation();
selectCallback(file);
});
}
try {
for(var _i0=0;_i0<l.length;_i0++) {
var file = l[_i0];
_lf0(file);
}
}catch(_err){}
})();
})();
return _e0;

}
export const TemplateSet_widgets = {
itemListing : TemplateSet_widgets_itemListing,
attributeListing : TemplateSet_widgets_attributeListing,
appName : TemplateSet_widgets_appName,
attributeDetail : TemplateSet_widgets_attributeDetail,
typeListing : TemplateSet_widgets_typeListing,
fileDetail : TemplateSet_widgets_fileDetail,
itemDetail : TemplateSet_widgets_itemDetail,
fileListing : TemplateSet_widgets_fileListing
};
function TemplateSet_fragments_appDisplay(_page) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
var p = _page;
var _dis = _page.event;
var model = _page.model;
_e0.className = 'fragment app-display';
(function() {
let widgets = TemplateSet_widgets;
var displayType;
(function() {
try{
if (!displayType) {
 displayType = 'items';
 }
}catch(_e){
console.error(_e);
}
})();
function changeDisplayType(type) {
try{
displayType = type;
 p.event('changeDisplay');
}catch(_e){
console.error(_e);
}
}
var _e1;
var _f1 = function() {
if (_e0) {
_rem(_e0,'_e1');
_e1 = null;
_e1 = _d.createElement('div');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.className = 'selector';
_e0.appendChild(_e1);
(function() {
var _e2;
_e2 = _d.createElement('span');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = 'option display-type '+((function(){try{return displayType=='items' ? 'selected' : ''!=undefined?displayType=='items' ? 'selected' : '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode('All Items');
_e2.appendChild(_t0);
}catch(_err){}
})();
_e2.addEventListener('click', function($event) {
$event.stopPropagation();
changeDisplayType('items');
});
var _e3;
_e3 = _d.createElement('span');
_e3._elt = '_e3';
_e3.$$remove = [];
_e3.className = 'option display-type '+((function(){try{return displayType=='types' ? 'selected' : ''!=undefined?displayType=='types' ? 'selected' : '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e3);
(function() {
try {
var _t0 = _d.createTextNode('Types');
_e3.appendChild(_t0);
}catch(_err){}
})();
_e3.addEventListener('click', function($event) {
$event.stopPropagation();
changeDisplayType('types');
});
var _e4;
_e4 = _d.createElement('span');
_e4._elt = '_e4';
_e4.$$remove = [];
_e4.className = 'option display-type '+((function(){try{return displayType=='systemAttributes' ? 'selected' : ''!=undefined?displayType=='systemAttributes' ? 'selected' : '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e4);
(function() {
try {
var _t0 = _d.createTextNode('System Attributes');
_e4.appendChild(_t0);
}catch(_err){}
})();
_e4.addEventListener('click', function($event) {
$event.stopPropagation();
changeDisplayType('systemAttributes');
});
var _e5;
_e5 = _d.createElement('span');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'option display-type '+((function(){try{return displayType=='sourceFiles' ? 'selected' : ''!=undefined?displayType=='sourceFiles' ? 'selected' : '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e5);
(function() {
try {
var _t0 = _d.createTextNode('Source Files');
_e5.appendChild(_t0);
}catch(_err){}
})();
_e5.addEventListener('click', function($event) {
$event.stopPropagation();
changeDisplayType('sourceFiles');
});
})();
var _x0 = ['_e1'];
if (_e1) {
_ins(_e0,_e1,_x0);
}
}
};
var _rv0 = _dis('changeDisplay',_f1);
_e0.$$remove.push(function() {_rv0();});
_f1();
var _e2;
_e2 = _d.createElement('div');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = 'vertical-spacing-10';
_e0.appendChild(_e2);
(function() {
null})();
var _e3;
_e3 = _d.createElement('div');
_e3._elt = '_e3';
_e3.$$remove = [];
_e3.className = 'data-listing-display';
_e0.appendChild(_e3);
(function() {
var selectedItem;
var selectedType;
var selectedAttribute;
var selectedFile;
function itemCallback(item) {
try{
selectedItem = item;
 selectedAttribute = selectedType = selectedFile = null;
 p.event('reselect');
}catch(_e){
console.error(_e);
}
}
function attributeCallback(attribute) {
try{
selectedAttribute = attribute;
 selectedItem = selectedType = selectedFile = null;
 p.event('reselect');
}catch(_e){
console.error(_e);
}
}
function typeCallback(t) {
try{
selectedType = t;
 selectedAttribute = selectedItem = selectedFile = null;
 p.event('reselect');
}catch(_e){
console.error(_e);
}
}
function fileCallback(t) {
try{
selectedFile = t;
 selectedAttribute = selectedItem = selectedType = null;
 p.event('reselect');
}catch(_e){
console.error(_e);
}
}
var _e4;
_e4 = _d.createElement('div');
_e4._elt = '_e4';
_e4.$$remove = [];
_e4.className = 'data-listing';
_e3.appendChild(_e4);
(function() {
var _e5;
var _f2 = function() {
if (_e4) {
_rem(_e4,'_e5');
_e5 = null;
var _b0 = false;
try {
_b0 = (displayType==='items');
} catch(_err) { }
if (_b0) {
_e5 = widgets.itemListing((function() {try {return selectedItem;}catch(err) { return undefined; } })(),(function() {try {return model.appData.allItems;}catch(err) { return undefined; } })(),(function() {try {return itemCallback;}catch(err) { return undefined; } })());
_e5._elt = '_e5';
_e4.appendChild(_e5);
}
var _x1 = ['_e5'];
if (_e5) {
_ins(_e4,_e5,_x1);
}
}
};
var _rv1 = _dis('reselect',_f2);
_e4.$$remove.push(function() {_rv1();});
var _rv2 = _dis('changeDisplay',_f2);
_e4.$$remove.push(function() {_rv2();});
var _rv3 = _dis('appDataChanged',_f2);
_e4.$$remove.push(function() {_rv3();});
_f2();
var _e6;
var _f3 = function() {
if (_e4) {
_rem(_e4,'_e6');
_e6 = null;
var _b0 = false;
try {
_b0 = (displayType==='sourceFiles');
} catch(_err) { }
if (_b0) {
_e6 = widgets.fileListing((function() {try {return selectedFile;}catch(err) { return undefined; } })(),(function() {try {return model.appData.sourceFiles;}catch(err) { return undefined; } })(),(function() {try {return fileCallback;}catch(err) { return undefined; } })());
_e6._elt = '_e6';
_e4.appendChild(_e6);
}
var _x2 = ['_e5','_e6'];
if (_e6) {
_ins(_e4,_e6,_x2);
}
}
};
var _rv4 = _dis('reselect',_f3);
_e4.$$remove.push(function() {_rv4();});
var _rv5 = _dis('changeDisplay',_f3);
_e4.$$remove.push(function() {_rv5();});
var _rv6 = _dis('appDataChanged',_f3);
_e4.$$remove.push(function() {_rv6();});
_f3();
var _e7;
var _f4 = function() {
if (_e4) {
_rem(_e4,'_e7');
_e7 = null;
var _b0 = false;
try {
_b0 = (displayType==='systemAttributes');
} catch(_err) { }
if (_b0) {
_e7 = widgets.attributeListing((function() {try {return selectedAttribute;}catch(err) { return undefined; } })(),(function() {try {return model.appData.allSystemAttributes;}catch(err) { return undefined; } })(),(function() {try {return attributeCallback;}catch(err) { return undefined; } })());
_e7._elt = '_e7';
_e4.appendChild(_e7);
}
var _x3 = ['_e5','_e6','_e7'];
if (_e7) {
_ins(_e4,_e7,_x3);
}
}
};
var _rv7 = _dis('reselect',_f4);
_e4.$$remove.push(function() {_rv7();});
var _rv8 = _dis('changeDisplay',_f4);
_e4.$$remove.push(function() {_rv8();});
var _rv9 = _dis('appDataChanged',_f4);
_e4.$$remove.push(function() {_rv9();});
_f4();
})();
var _e5;
_e5 = _d.createElement('div');
_e5._elt = '_e5';
_e5.$$remove = [];
_e5.className = 'data-details';
_e3.appendChild(_e5);
(function() {
var _e6;
var _f2 = function() {
if (_e5) {
_rem(_e5,'_e6');
_e6 = null;
var _b0 = false;
try {
_b0 = (selectedItem && displayType==='items');
} catch(_err) { }
if (_b0) {
_e6 = widgets.itemDetail((function() {try {return selectedItem;}catch(err) { return undefined; } })());
_e6._elt = '_e6';
_e5.appendChild(_e6);
}
var _x1 = ['_e6'];
if (_e6) {
_ins(_e5,_e6,_x1);
}
}
};
var _rv1 = _dis('reselect',_f2);
_e5.$$remove.push(function() {_rv1();});
var _rv2 = _dis('changeDisplay',_f2);
_e5.$$remove.push(function() {_rv2();});
_f2();
var _e7;
var _f3 = function() {
if (_e5) {
_rem(_e5,'_e7');
_e7 = null;
var _b0 = false;
try {
_b0 = (selectedAttribute && displayType==='systemAttributes');
} catch(_err) { }
if (_b0) {
_e7 = widgets.attributeDetail((function() {try {return selectedAttribute;}catch(err) { return undefined; } })());
_e7._elt = '_e7';
_e5.appendChild(_e7);
}
var _x2 = ['_e6','_e7'];
if (_e7) {
_ins(_e5,_e7,_x2);
}
}
};
var _rv3 = _dis('reselect',_f3);
_e5.$$remove.push(function() {_rv3();});
var _rv4 = _dis('changeDisplay',_f3);
_e5.$$remove.push(function() {_rv4();});
_f3();
var _e8;
var _f4 = function() {
if (_e5) {
_rem(_e5,'_e8');
_e8 = null;
var _b0 = false;
try {
_b0 = (selectedFile && displayType==='sourceFiles');
} catch(_err) { }
if (_b0) {
_e8 = widgets.fileDetail((function() {try {return selectedFile;}catch(err) { return undefined; } })());
_e8._elt = '_e8';
_e5.appendChild(_e8);
}
var _x3 = ['_e6','_e7','_e8'];
if (_e8) {
_ins(_e5,_e8,_x3);
}
}
};
var _rv5 = _dis('reselect',_f4);
_e5.$$remove.push(function() {_rv5();});
var _rv6 = _dis('changeDisplay',_f4);
_e5.$$remove.push(function() {_rv6();});
_f4();
})();
})();
})();
return _e0;

}
export const TemplateSet_fragments = {
appDisplay : TemplateSet_fragments_appDisplay
};
function TemplateSet_pages_ConsolePage(_page) {
var _d = document;
var _e0;
_e0 = _d.createElement('div');
_e0._elt = '_e0';
_e0.$$remove = [];
var _model = _page.model;
var p = _page;
var m = _page.model;
var _dis = _page.event;
_e0.className = 'page console-page';
(function() {
let fragments = TemplateSet_fragments;
var _e1;
var _f0 = function() {
if (_e0) {
_rem(_e0,'_e1');
_e1 = null;
_e1 = _d.createElement('div');
_e1._elt = '_e1';
_e1.$$remove = [];
_e1.className = 'selector';
_e0.appendChild(_e1);
(function() {
var _e2;
var _f1 = function() {
if (_e1) {
_rem(_e1,'_e2');
_e2 = null;
var _lf0 = function(name){
_e2 = _d.createElement('span');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = 'option app '+((function(){try{return m.appName == name ? 'selected' : ''!=undefined?m.appName == name ? 'selected' : '':'';}catch(_e){return '';}})())+'';
_e1.appendChild(_e2);
(function() {
try {
var _t0 = _d.createTextNode(''+((function(){try{return name!=undefined?name:'';}catch(_e){return '';}})())+'');
_e2.appendChild(_t0);
}catch(_err){}
})();
_e2.addEventListener('click', function($event) {
$event.stopPropagation();
window.selectApp(name);
});
}
try {
for(var _i0=0;_i0<m.appNames.length;_i0++) {
var name = m.appNames[_i0];
_lf0(name);
}
}catch(_err){}
var _x0 = ['_e2'];
if (_e2) {
_ins(_e1,_e2,_x0);
}
}
};
var _rv0 = _dis('appNameChanged',_f1);
_e1.$$remove.push(function() {_rv0();});
var _rv1 = _dis('appNamesChanged',_f1);
_e1.$$remove.push(function() {_rv1();});
_f1();
})();
var _x0 = ['_e1'];
if (_e1) {
_ins(_e0,_e1,_x0);
}
}
};
var _rv0 = _dis('appNamesChanged',_f0);
_e0.$$remove.push(function() {_rv0();});
var _rv1 = _dis('appNameChanged',_f0);
_e0.$$remove.push(function() {_rv1();});
_f0();
function _f1() {
try{
console.log('updating item map on window');
 window.itemMap = {
}
;
 if (!m.appData) return;
 let data = m.appData;
 debugger;
 data.allItems.forEach(item => {
 let key = ''+item.id;
 window.itemMap[key] = item;
 }
);
 p.event('itemsChanged');
}catch(_e){
console.error(_e);
}
}
var _cb0 = _dis('appDataChanged',_f1);
_e0.$$remove.push(_cb0);
(function() {
try{
window.getItemName = (id) => {
 let key = ''+id;
 if (!window.itemMap) {
 return 'NoMap';
 }
 if (window.itemMap[key]) {
 return window.itemMap[key].name;
 }
 else {
 return 'undefined';
 }
 }
;
}catch(_e){
console.error(_e);
}
})();
var _e2;
_e2 = _d.createElement('div');
_e2._elt = '_e2';
_e2.$$remove = [];
_e2.className = 'vertical-spacing-10';
_e0.appendChild(_e2);
(function() {
null})();
var _e3;
_e3 = fragments.appDisplay(_page);
_e3._elt = '_e3';
_e0.appendChild(_e3);
})();
return _e0;

}
export const TemplateSet_pages = {
ConsolePage : TemplateSet_pages_ConsolePage
};
export function ConsolePage() {
var _eventDispatcher = EventDispatcher();
function _init(_parent,_element) {
console.log('init page ConsolePage');
if ((!_parent) && (!_element)) {
console.error('No parent or element passed to init()');
return;
}
let pages = TemplateSet_pages;
var _elt = pages.ConsolePage(_obj);
_obj.view = _elt;
if (_element) {
_parent.replaceChild(_elt,_element);
} else {
_parent.appendChild(_elt);
}
return _obj;
}
var _obj = {
event: function(event,callback) { return _eventDispatcher.event(event,callback); }
, init: _init
};
_obj.model = (function() {
var _appNames;
var _appName;
var _appData;
return {
set appNames(appNames) {
if (appNames===_appNames) return;
_appNames = appNames;
_eventDispatcher.event('appNamesChanged');
}
,get appNames() { return _appNames;} 
,set appName(appName) {
if (appName===_appName) return;
_appName = appName;
_eventDispatcher.event('appNameChanged');
}
,get appName() { return _appName;} 
,set appData(appData) {
if (appData===_appData) return;
_appData = appData;
_eventDispatcher.event('appDataChanged');
}
,get appData() { return _appData;} 
};
})();
return _obj;

};
