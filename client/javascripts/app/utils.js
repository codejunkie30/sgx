var D= new Date('2011-06-02T09:34:29+02:00');
if(!D || +D!== 1307000069000){
    Date.fromISO= function(s){
    	return fixIEDate(s);
    }
}
else{
    Date.fromISO= function(s){
    	var ret = new Date(s);
    	if (!isValidDate(ret)) ret = fixIEDate(s);
        return ret;
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

function fixIEDate(s) {
	s = s.split(/\D/);
	return new Date(Date.UTC(s[0], --s[1]||'', s[2]||'', s[3]||'', s[4]||''));
}

function isValidDate(d) {
	if ( Object.prototype.toString.call(d) !== "[object Date]" ) return false;
	return !isNaN(d.getTime());
}