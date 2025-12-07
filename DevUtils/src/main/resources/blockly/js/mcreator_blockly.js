Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_block_with_plus_one',
  weight: 220,
  displayText: function(scope) {
    return devUtils.t("devutils.contextmenu.plus_one");
  },
  preconditionFn: function(scope) {
    const block = scope.block;
    if (
      block &&
      block.outputConnection &&
      !block.isShadow() &&
      Array.isArray(block.outputConnection.check_) && block.type != "math_number" &&
      block.outputConnection.check_.includes('Number') && devUtils.isEnableContextMenu('wrap_block_with_plus_one')
    ) {
      return 'enabled';
    }
    return 'hidden';
  },
  callback: function(scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    const xy = oldBlock.getRelativeToSurfaceXY();

    const parentConnection = oldBlock.outputConnection?.targetConnection;

    const newBlock = workspace.newBlock('math_dual_ops');
    newBlock.setFieldValue('ADD', 'OP');
    newBlock.initSvg();
    newBlock.render();

    const A = newBlock.getInput('A').connection;
    const B = newBlock.getInput('B').connection;

    if (parentConnection) parentConnection.disconnect();

    A.connect(oldBlock.outputConnection);

    const oneBlock = workspace.newBlock('math_number');
    oneBlock.setFieldValue('1', 'NUM');
    oneBlock.initSvg();
    oneBlock.render();
    B.connect(oneBlock.outputConnection);
    newBlock.moveBy(xy.x, xy.y);
    if (parentConnection) {
      parentConnection.connect(newBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'copy_selected_block_as_xml',
  weight: 250,
  displayText: function (scope) {
    return 'Copy To XML';
  },
  preconditionFn: function (scope) {
    return scope.block && !scope.block.isShadow() && devUtils.isEnableContextMenu('copy_selected_block_as_xml') ? 'enabled' : 'hidden';
  },
callback: function (scope) {
    const block = scope.block;
    if (!block) return;

    const dom = Blockly.Xml.blockToDom(block);

    const next = dom.querySelector('next');
    if (next) next.remove();

    const withIds = dom.querySelectorAll('[id]');
    for (let i = 0; i < withIds.length; i++) {
      withIds[i].removeAttribute('id');
    }
    if (dom.hasAttribute('id')) {
      dom.removeAttribute('id');
    }
    const serializer = new XMLSerializer();
    let xmlText = serializer.serializeToString(dom);

    devUtils.setClipboard(xmlText);
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});
const mapping = {
  'entity_from_deps': 'entity_iterator',
  'immediate_source_entity_from_deps': 'entity_iterator',
  'itemstack_to_mcitem': 'itemstack_iterator',
  'source_entity_from_deps': 'entity_iterator',
  'direction_from_deps': 'direction_iterator'
};
Blockly.ContextMenuRegistry.registry.register({
  id: 'replace_with_iterator',
  weight: 200,
  displayText: function (scope) {
    return devUtils.t("devutils.contextmenu.replace_with_iterator");
  },
  preconditionFn: function (scope) {
    if (!scope.block || scope.block.isShadow()) return 'hidden';

    const id = 'replace_with_iterator';
    if (!devUtils.isEnableContextMenu(id)) return 'hidden';

    if (mapping[scope.block.type]) {
      return 'enabled';
    }
    return 'hidden';
  },
  callback: function (scope) {
    const block = scope.block;
    const workspace = block.workspace;
    const xy = block.getRelativeToSurfaceXY();

    const newType = mapping[block.type];
    if (!newType) return;

    // 保存连接信息
    const parentConnection = block.outputConnection || block.previousConnection;
    const targetConnection = parentConnection && parentConnection.targetConnection;

    // 创建新块
    const newBlock = workspace.newBlock(newType);
    newBlock.initSvg();
    newBlock.render();
    newBlock.moveBy(xy.x, xy.y);

    // 重新连接
    if (targetConnection) {
      const newConn = newBlock.outputConnection || newBlock.previousConnection;
      if (newConn) newConn.connect(targetConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_text_in_join',
  weight: 210,
  displayText: function (scope) {
    return devUtils.t("devutils.contextmenu.wrap_text_in_join");
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const output = block.outputConnection && block.outputConnection.check_;
    if (output && output.includes('String') && devUtils.isEnableContextMenu('wrap_text_in_join')) {
      return 'enabled';
    }
    return 'hidden';
  },
  callback: function (scope) {
    const block = scope.block;
    const workspace = block.workspace;
    if (!block) return;

    if (block.type === 'text_join') {
      // 获取当前 items 数量
      const mut = block.mutationToDom();
      const currentItems = parseInt(mut.getAttribute('items')) || 0;
      const newItems = currentItems + 1;
      mut.setAttribute('items', newItems);
      block.domToMutation(mut);

      // 创建空字符串块
      const emptyTextBlock = workspace.newBlock('text');
      emptyTextBlock.setFieldValue('', 'TEXT');
      emptyTextBlock.initSvg();
      emptyTextBlock.render();

      // 连接空字符串到新输入
      const input = block.getInput('ADD' + (newItems - 1));
      if (input && input.connection) {
        input.connection.connect(emptyTextBlock.outputConnection);
      }
      return;
    }

    // ✅ 情况 2：否则创建新的 text_join 来包裹该块
    const parentConnection = block.outputConnection?.targetConnection;
    const xy = block.getRelativeToSurfaceXY();

    const joinBlock = workspace.newBlock('text_join');
    joinBlock.initSvg();
    joinBlock.render();

    // 设置 text_join 至少 2 项
    const mut = joinBlock.mutationToDom();
    mut.setAttribute('items', 2);
    joinBlock.domToMutation(mut);

    // 创建空字符串块
    const emptyTextBlock = workspace.newBlock('text');
    emptyTextBlock.setFieldValue('', 'TEXT');
    emptyTextBlock.initSvg();
    emptyTextBlock.render();

    // 放置位置
    joinBlock.moveBy(xy.x, xy.y);

    // 若该块原本连接在上层，则断开
    if (parentConnection) {
      parentConnection.disconnect();
    }

    // 连接原 block 到 ADD0
    joinBlock.getInput('ADD0').connection.connect(block.outputConnection);

    // 连接空字符串到 ADD1
    joinBlock.getInput('ADD1').connection.connect(emptyTextBlock.outputConnection);

    // 若原本连接在别的块上，则重新接上
    if (parentConnection) {
      parentConnection.connect(joinBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});

Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_entity_in_compare',
  weight: 210,
  displayText: function (scope) {
    return devUtils.t("devutils.contextmenu.wrap_entity_in_compare");
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';
    const output = block.outputConnection && block.outputConnection.check_;
    if (output && output.includes('Entity') && devUtils.isEnableContextMenu('wrap_entity_in_compare')) {
      return 'enabled';
    }
    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    if (!oldBlock) return;

    // 保存父连接和位置
    const parentConnection = oldBlock.outputConnection?.targetConnection;
    const xy = oldBlock.getRelativeToSurfaceXY();

    // 创建 logic_entity_compare 块
    const compareBlock = workspace.newBlock('logic_entity_compare');
    compareBlock.initSvg();
    compareBlock.render();
    compareBlock.moveBy(xy.x, xy.y);

    // 若旧块有上层连接，则断开
    if (parentConnection) {
      parentConnection.disconnect();
    }

    // 连接旧块到 compareTo
    const compareToInput = compareBlock.getInput('compareTo')?.connection;
    if (compareToInput) {
      compareToInput.connect(oldBlock.outputConnection);
    }

    // 若旧块原本连接在某个表达式上，则让新块接上
    if (parentConnection) {
      parentConnection.connect(compareBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});
// 通用函数：复制旧块的基本属性并替换为新块
function replaceVariableBlock(oldBlock, newType) {
  const workspace = oldBlock.workspace;
  const parentConnection =
    oldBlock.outputConnection?.targetConnection ||
    oldBlock.previousConnection?.targetConnection;
  const nextBlock = oldBlock.nextConnection?.targetBlock();
  const xy = oldBlock.getRelativeToSurfaceXY();

  // 保存字段和 mutation
  const mutation = oldBlock.mutationToDom();
  const fieldValue = oldBlock.getFieldValue('VAR');

  // 删除旧块
  oldBlock.dispose(false);

  // 创建新块
  const newBlock = workspace.newBlock(newType);
  if (mutation) newBlock.domToMutation(mutation);
  if (fieldValue) newBlock.setFieldValue(fieldValue, 'VAR');

  newBlock.initSvg();
  newBlock.render();
  newBlock.moveBy(xy.x, xy.y);

  // 若有上层连接
  if (parentConnection) {
    const connection =
      parentConnection.sourceBlock_.outputConnection
        ? newBlock.outputConnection
        : newBlock.previousConnection;
    if (connection && parentConnection.checkType_(connection)) {
      parentConnection.connect(connection);
    }
  }

  // 若原本有下方连接
  if (nextBlock && newBlock.nextConnection) {
    newBlock.nextConnection.connect(nextBlock.previousConnection);
  }
}
Blockly.ContextMenuRegistry.registry.register({
  id: 'variables_get_to_set',
  weight: 210,
  displayText: function () {
    const id = 'variables_get_to_set';
    return typeof devUtils !== 'undefined'
      ? devUtils.t(`devutils.contextmenu.${id}`)
      : '转换为 Set 变量';
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'variables_get_to_set';
    if (typeof devUtils !== 'undefined' && !devUtils.isEnableContextMenu(id)) {
      return 'hidden';
    }

    if (/^variables_get_/.test(block.type)) return 'enabled';
    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const newType = oldBlock.type.replace('variables_get_', 'variables_set_');
    replaceVariableBlock(oldBlock, newType);
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'variables_set_to_get',
  weight: 209,
  displayText: function () {
    const id = 'variables_set_to_get';
    return typeof devUtils !== 'undefined'
      ? devUtils.t(`devutils.contextmenu.${id}`)
      : '转换为 Get 变量';
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'variables_set_to_get';
    if (typeof devUtils !== 'undefined' && !devUtils.isEnableContextMenu(id)) {
      return 'hidden';
    }

    if (/^variables_set_/.test(block.type)) return 'enabled';
    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const newType = oldBlock.type.replace('variables_set_', 'variables_get_');
    replaceVariableBlock(oldBlock, newType);
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_compare_mcitems',
  weight: 220,
  displayText: function () {
    return devUtils.t('devutils.contextmenu.wrap_compare_mcitems');
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'wrap_compare_mcitems';
    if (!devUtils.isEnableContextMenu(id)) return 'hidden';

    // 假设输出类型为 ItemStack 的块可被包裹
    const output = block.outputConnection && block.outputConnection.check_;
    if (output && (output.includes('MCItem') || output.includes("MCItemBlock"))) return 'enabled';

    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    if (!oldBlock) return;

    const xy = oldBlock.getRelativeToSurfaceXY();
    const parentConnection = oldBlock.outputConnection?.targetConnection;

    // 创建 compare_mcitems 块
    const compareBlock = workspace.newBlock('compare_mcitems');
    compareBlock.initSvg();
    compareBlock.render();
    compareBlock.moveBy(xy.x, xy.y);

    // 创建 b 的默认块 mcitem_all
    const bBlock = workspace.newBlock('mcitem_all');
    bBlock.initSvg();
    bBlock.render();
    bBlock.setFieldValue('Blocks.AIR', 'value');

    // 连接 a -> 选中的块
    const aInput = compareBlock.getInput('a')?.connection;
    if (aInput) aInput.connect(oldBlock.outputConnection);

    // 连接 b -> 默认块
    const bInput = compareBlock.getInput('b')?.connection;
    if (bInput) bInput.connect(bBlock.outputConnection);

    // 如果原块有父连接，则连接新块
    if (parentConnection && compareBlock.outputConnection) {
      parentConnection.disconnect();
      parentConnection.connect(compareBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_math_binary_ops',
  weight: 220,
  displayText: function () {
    return devUtils.t('devutils.contextmenu.wrap_math_binary_ops');
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'wrap_math_binary_ops';
    if (!devUtils.isEnableContextMenu(id)) return 'hidden';

    // 仅允许输出数值类型的块使用
    const output = block.outputConnection && block.outputConnection.check_;
    if (output && output.includes('Number')) return 'enabled';

    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    if (!oldBlock) return;

    const xy = oldBlock.getRelativeToSurfaceXY();
    const parentConnection = oldBlock.outputConnection?.targetConnection;

    // 创建 math_binary_ops 块
    const newBlock = workspace.newBlock('math_binary_ops');
    newBlock.initSvg();
    newBlock.render();
    newBlock.moveBy(xy.x, xy.y);

    // 设置 OP 默认字段（保持 EQ）
    newBlock.setFieldValue('EQ', 'OP');

    // 创建默认的 B 输入块：math_number = 1
    const bBlock = workspace.newBlock('math_number');
    bBlock.initSvg();
    bBlock.render();
    bBlock.setFieldValue('1', 'NUM');

    // 连接 A -> 原块
    const aInput = newBlock.getInput('A')?.connection;
    if (aInput) aInput.connect(oldBlock.outputConnection);

    // 连接 B -> 默认块
    const bInput = newBlock.getInput('B')?.connection;
    if (bInput) bInput.connect(bBlock.outputConnection);

    // 若原块有父连接则重接
    if (parentConnection && newBlock.outputConnection) {
      parentConnection.disconnect();
      parentConnection.connect(newBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_controls_if',
  weight: 220,
  displayText: function () {
    return devUtils.t('devutils.contextmenu.wrap_controls_if');
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'wrap_controls_if';
    if (!devUtils.isEnableContextMenu(id)) return 'hidden';

    // 仅允许输出布尔类型的块
    const output = block.outputConnection && block.outputConnection.check_;
    if (output && output.includes('Boolean')) return 'enabled';

    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    if (!oldBlock) return;

    const xy = oldBlock.getRelativeToSurfaceXY();
    const parentConnection = oldBlock.outputConnection?.targetConnection;

    // 创建 controls_if 块
    const ifBlock = workspace.newBlock('controls_if');
    ifBlock.initSvg();
    ifBlock.render();
    ifBlock.moveBy(xy.x, xy.y);

    // 连接 IF0 -> 选中的布尔块
    const ifInput = ifBlock.getInput('IF0')?.connection;
    if (ifInput) ifInput.connect(oldBlock.outputConnection);

    // 若原块有父连接，则重新接回
    if (parentConnection && ifBlock.outputConnection) {
      parentConnection.disconnect();
      parentConnection.connect(ifBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});
Blockly.ContextMenuRegistry.registry.register({
  id: 'wrap_logic_negate',
  weight: 220,
  displayText: function () {
    return devUtils.t('devutils.contextmenu.wrap_logic_negate');
  },
  preconditionFn: function (scope) {
    const block = scope.block;
    if (!block) return 'hidden';

    const id = 'wrap_logic_negate';
    if (!devUtils.isEnableContextMenu(id)) return 'hidden';

    if (block.type === 'logic_negate') return 'hidden';
    // 仅允许输出 Boolean 类型的积木
    const output = block.outputConnection && block.outputConnection.check_;
    if (output && output.includes('Boolean')) return 'enabled';

    return 'hidden';
  },
  callback: function (scope) {
    const oldBlock = scope.block;
    const workspace = oldBlock.workspace;
    if (!oldBlock) return;

    const xy = oldBlock.getRelativeToSurfaceXY();
    const parentConnection = oldBlock.outputConnection?.targetConnection;

    // 创建 logic_negate 块
    const negateBlock = workspace.newBlock('logic_negate');
    negateBlock.initSvg();
    negateBlock.render();
    negateBlock.moveBy(xy.x, xy.y);

    // 连接 BOOL -> 原块
    const boolInput = negateBlock.getInput('BOOL')?.connection;
    if (boolInput) boolInput.connect(oldBlock.outputConnection);

    // 若原块有父连接，则接回去
    if (parentConnection && negateBlock.outputConnection) {
      parentConnection.disconnect();
      parentConnection.connect(negateBlock.outputConnection);
    }
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK
});



