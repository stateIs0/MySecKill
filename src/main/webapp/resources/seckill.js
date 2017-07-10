// 存放主要交互逻辑代码
// javascript 模块化
// 使用json对象模拟出分包的概念，便于维护
//seckill.detail.init(params) 类似包名.类名.方法（参数）
var seckill ={
  // 封装秒杀相关ajas的url
  url:{
    now:function () {
      return "/seckill/time/now";
    },
    exposer:function (seckillId) {
      return '/seckill/' + seckillId + '/exposer';
    },
    execution:function (seckillId, exposer) {
      return '/seckill/' + seckillId + '/' + exposer.md5 + '/execution';
    }

  },
  // 验证手机号
  validatePhone:function (phone) {
    if (phone && phone.length == 11 && !isNaN(phone)){
      return true;
    }
    return false;
  },
  countDown:function (seckillId, nowTime, startTime, endTime) {
    var seckillBox = $("#seckill-box");
    // 手机判断
    if (nowTime > endTime) {
      // 秒杀结束
      seckillBox.html('秒杀结束');
    } else if (nowTime < startTime){
      // 秒杀未开始，计时
      var killTime = new Date(startTime + 1000);
      seckillBox.countdown(killTime,function (event) {
        var format = event.strftime('秒杀计时：%D天 %H时 %M分 %S秒');
        seckillBox.html(format);
        //计时完成后回调事件
      }).on('finish.countdown', function () {
        //获取秒杀地址,控制显示逻辑，执行秒杀操作
      });

    } else {
      // 秒杀开始
      seckill.handleSeckill(seckillId, seckillBox);
    }

  },
  // 处理秒杀逻辑
  handleSeckill:function (seckillId, node) {
    //获取秒杀地址,控制显示逻辑，执行秒杀操作
    node.hide().
    html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
    var url = seckill.url.exposer(seckillId);
    $.get(url,{},function (result) {
      // 在回调函数中，执行交互流程
      if (result && result.success) {
        var exposer = result.data;
        if (exposer.exposed){
          // 开启秒杀
          // 获取秒杀地址
          var killUrl = seckill.url.execution(seckillId,exposer);
          console.log(killUrl);// todo
          // 绑定一次点击事件，如果直接使用click则是永久绑定。防止用户连续点击
          $("#killBtn").one('click',function () {
            // 执行秒杀请求的操作
            // 1:禁用按钮
            $(this).addClass("disabled");
            //2 :发送秒杀请求,执行秒杀
            $.post(killUrl,{},function (result) {
              if(result && result.success == true){3
                var killResult = result.data;
                var state = killResult.state;
                var stateInfo = killResult.stateInfo;
                //显示秒杀结果
                node.html('<span class="label label-success">' + stateInfo +'</span>')
              }
            });
          });
          node.show("slow");
        }else {
          // 未开始秒杀
          var nowTime = exposer.now;
          var start = exposer.start;
          var end = exposer.end;
          // 重新进入计时逻辑
          seckill.countDown(seckillId,nowTime,start,end);
        }
      } else {
        console.log(result);
      }

    });
  },
  // 秒杀详情页逻辑
  detail:{
    // 详情页初始化
    init:function (params) {
      // 1:用户手机验证相关的操作，计时交互
      // 规划我们的交互流程
      // 在cookie中查找手机号
      var killPhone = $.cookie("killPhone");
      var startTime = params['startTime'];
      var endTime = params['endTime'];
      var seckillId = params['seckillId'];
      if (!seckill.validatePhone(killPhone)){
        // 绑定phone
        // 控制输出
        // 显示弹出层
        var killPhoneModal = $("#killPhoneModal");
        killPhoneModal.modal({
          show:true,//显示键盘事件弹出层
          backdrop:'static',// 禁止位置关闭
          keyboard:false// 关闭
        });
        $("#killPhoneBtn").click(function () {
          //
          var inputPhone = $("#killPhoneKey").val();
          if (seckill.validatePhone(inputPhone)){
            // 电话写入cookie 有效期7天，只在seckill路径下生效
            $.cookie("killPhone",inputPhone,{expires:7,path:'/seckill'});
            // 刷新页面
            window.location.reload();
          } else {
            $("#killPhoneMessage").hide().html("<label class='label label-danger'>手机号错误!</label>").show("slow");
          }
        });
      }
      // 已经登录
      // 计时交互
      var startTime = params['startTime'];
      var endTime = params['endTime'];
      var seckillId = params['seckillId'];
      $.get(seckill.url.now(),{},function (result) {
        if (result && result.success == true){
          var nowTime = result.data;
          //手机判断
          seckill.countDown(seckillId,nowTime,startTime,endTime);
        } else {
          console.log(result)
        }
      });

    }
      


  }
}