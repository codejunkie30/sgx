define(function () { var templates = {};

//
// Source file: [/Volumes/Hard Drive/Sites/_ideas/WMSI2/assets/javascripts/app/template/another-example.template]
// Template name: [another-example]
//
templates['another-example'] = function(obj){
var __t,__p='',__j=Array.prototype.join,print=function(){__p+=__j.call(arguments,'');};
with(obj||{}){
__p+='And this is coming from another plain HTML template that is concatenated into a single template file with the others.\n';
}
return __p;
}()

//
// Source file: [/Volumes/Hard Drive/Sites/_ideas/WMSI2/assets/javascripts/app/template/example.template]
// Template name: [example]
//
templates['example'] = function(obj){
var __t,__p='',__j=Array.prototype.join,print=function(){__p+=__j.call(arguments,'');};
with(obj||{}){
__p+='<div class="template">This is coming from a plain HTML template</div>\n<div class="styled">And it has all been styled (poorly)</div>';
}
return __p;
}()
return templates; });