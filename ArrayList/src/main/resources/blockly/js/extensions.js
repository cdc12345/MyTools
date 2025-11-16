const TYPE_COLOR_MAP = {
    "BlockState": 60,
    "Entity": 195,
    "ItemStack": 350,
    "Text": 160,
    "Number": 230,
    "Logic": 210,
    "Direction": 20,
    "DamageSource": 320,
    "Vector": 270
};

Blockly.Extensions.register('type_color_extension', function () {
    const block = this;

    // 更新颜色函数
    block.updateColorByType = function () {
        const type = block.getFieldValue('type');
        const color = TYPE_COLOR_MAP[type] || 40; // 默认颜色
        block.setColour(color);
    };

    // 初始化执行一次
    block.updateColorByType();

    // 监听字段变化
    block.setOnChange(function (event) {
        if (event.type === Blockly.Events.BLOCK_CHANGE &&
            event.blockId === block.id &&
            event.name === 'type') {

            block.updateColorByType();
        }
    });
});