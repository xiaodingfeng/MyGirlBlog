layui.use(['layer','upload'] ,function(){
    var layer = layui.layer;
    var upload = layui.upload;

//执行实例
    var uploadInst = upload.render({
        elem: '#backImage' //绑定元素
        ,url: '/uploadImage' //上传接口
        ,accept:'images'
        ,done: function(res){
            //上传完毕回调
        }
        ,error: function(){
            layer.msg('请求异常，请重试', {shift: 6},{icon: 5});
        }
    });

});
