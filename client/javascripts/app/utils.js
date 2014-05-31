var D= new Date('2011-06-02T09:34:29+02:00');
if(!D || +D!== 1307000069000){
    Date.fromISO= function(s){
    	var ret = fixIEDate(s);
    	return toSGT(ret);
    }
}
else{
    Date.fromISO= function(s){
    	var ret = new Date(s);
    	if (!isValidDate(ret)) ret = fixIEDate(s);
        return toSGT(ret);
    }
}

Array.prototype.remove= function(){
    var what, a= arguments, L= a.length, ax;
    while(L && this.length){
        what= a[--L];
        while((ax= this.indexOf(what))!= -1){
            this.splice(ax, 1);
        }
    }
    return this;
}

var getPropIE = function ( name ) {
	 
    return Math.max(
        document.documentElement["client" + name],
        document.documentElement["scroll" + name],
        document.body["scroll" + name]
    );

}

function toSGT(dt) {
	var lt = dt.getTime();
	var lo = dt.getTimezoneOffset() * 60000;
	var utc = lt + lo;
	var sgt = utc + (3600000*8);
	return new Date(sgt);
}

function fixIEDate(s) {
	s = s.split(/\D/);
	return new Date(Date.UTC(s[0], --s[1]||'', s[2]||'', s[3]||'', s[4]||''));
}

function isValidDate(d) {
	if ( Object.prototype.toString.call(d) !== "[object Date]" ) return false;
	return !isNaN(d.getTime());
}

var ie1011Styles = [ 'msTouchAction', 'msWrapFlow', 'msWrapMargin', 'msWrapThrough', 'msOverflowStyle', 'msScrollChaining', 'msScrollLimit', 'msScrollLimitXMin', 'msScrollLimitYMin', 'msScrollLimitXMax', 'msScrollLimitYMax', 'msScrollRails', 'msScrollSnapPointsX', 'msScrollSnapPointsY', 'msScrollSnapType', 'msScrollSnapX', 'msScrollSnapY', 'msScrollTranslation', 'msFlexbox', 'msFlex', 'msFlexOrder', 'msTextCombineHorizontal' ];

function isAnyIE() {
	var myNav = navigator.userAgent.toLowerCase();
	var d = document,  b = d.body, s = b.style, ret = myNav.indexOf('msie') != -1;
	$.each(ie1011Styles, function(idx, property) { if (typeof s[property] !== "undefined") ret = true; });
	return ret;
}