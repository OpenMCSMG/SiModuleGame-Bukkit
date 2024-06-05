mc.broadcast('onEnable');
/**
 * 事件监听
 * en 传实体
 * et 传实体类型
 * bl 受影响的方块
 * cancel 传关闭事件
 */
// let id = eventBus.on('entity.explode', function (en, et, bL) {
//     console.log('entity.explode');
// });
// eventBus.remove(id)
/**
 * 创建task  主参数传 function
 * !!! 异步速率不能低于秒并且异步创建不能超过 2 个不然会出现线程异常
 * 如果超过两个可自己写插件实现了
 */
// scheduler.delay(
//     function () {
//         console.log('scheduler.delay');
//     }, 20, false);

CreatePixScript.make(
    '脚本演示',
    "小玄易",
    '1.0',
    "这里写脚本介绍"
)

function chatFunc() {
    mc.broadcast('chatFunc');
}

function gift() {
    mc.online().forEach(a => {
        utils.title(a, "HI你看好玩不", "§7自己动手写个BFUNC吧", 20, 40, 20);
    });
}