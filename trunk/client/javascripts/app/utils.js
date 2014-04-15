var D= new Date('2011-06-02T09:34:29+02:00');
if(!D || +D!== 1307000069000){
    Date.fromISO= function(s){
	  s = s.split(/\D/);
	  return new Date(Date.UTC(s[0], --s[1]||'', s[2]||'', s[3]||'', s[4]||'', s[5]||'', s[6]||''))
    }
}
else{
    Date.fromISO= function(s){
        return new Date(s);
    }
}