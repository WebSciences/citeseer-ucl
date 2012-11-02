function addToCartProxy(cid) {
 var spanid = 'cmsg_'+cid;
 document.getElementById(spanid).innerHTML = "processing...";
 MetadataCartJS.addToCart(cid, {
  callback:function(data) {
   cartCallback(data,spanid);
  }
 });
}
function cartCallback(data, spanid) {
 document.getElementById(spanid).innerHTML = data;
}
