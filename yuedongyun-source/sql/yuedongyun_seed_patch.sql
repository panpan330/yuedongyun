-- 悦动云：训练分类、训练训练与训练详情补丁

UPDATE `category` SET `name` = '运动训练' WHERE `id` = 1001;
UPDATE `category` SET `name` = '体态塑形' WHERE `id` = 1002;
UPDATE `category` SET `name` = '饮食管理' WHERE `id` = 1003;
UPDATE `category` SET `name` = '专项提升' WHERE `id` = 1004;
UPDATE `category` SET `name` = '会员成长' WHERE `id` = 1005;
UPDATE `category` SET `name` = '运动健康' WHERE `id` = 1006;
UPDATE `category` SET `name` = '运动商城' WHERE `id` = 1007;
UPDATE `category` SET `name` = '企业健身' WHERE `id` = 1008;

UPDATE `category` SET `name` = '燃脂训练' WHERE `id` = 2001;
UPDATE `category` SET `name` = '增肌塑形' WHERE `id` = 2002;
UPDATE `category` SET `name` = '居家训练' WHERE `id` = 2003;
UPDATE `category` SET `name` = '团课跟练' WHERE `id` = 2004;
UPDATE `category` SET `name` = '户外跑步' WHERE `id` = 2005;
UPDATE `category` SET `name` = '综合体能' WHERE `id` = 2054;

UPDATE `category` SET `name` = 'HIIT燃脂' WHERE `id` = 3003;
UPDATE `category` SET `name` = '力量进阶' WHERE `id` = 3007;
UPDATE `category` SET `name` = '功能训练' WHERE `id` = 3381;
UPDATE `category` SET `name` = '体能专项' WHERE `id` = 3395;

UPDATE `training`
SET `name` = '燃脂 HIIT 入门课', `first_cate_id` = 1001, `second_cate_id` = 2001, `third_cate_id` = 3003,
    `free` = 1, `price` = 0, `difficulty` = 1, `train_part` = '全身', `calorie_burn` = 260
WHERE `id` = 1;
UPDATE `training`
SET `name` = '居家核心激活课', `first_cate_id` = 1001, `second_cate_id` = 2003, `third_cate_id` = 3381,
    `free` = 1, `price` = 0, `difficulty` = 1, `train_part` = '核心', `calorie_burn` = 180
WHERE `id` = 2;
UPDATE `training`
SET `name` = '臀腿塑形进阶营', `first_cate_id` = 1001, `second_cate_id` = 2002, `third_cate_id` = 3007,
    `free` = 0, `price` = 9900, `difficulty` = 2, `train_part` = '臀腿', `calorie_burn` = 420
WHERE `id` = 3;
UPDATE `training`
SET `name` = '胸肩力量提升课', `first_cate_id` = 1001, `second_cate_id` = 2002, `third_cate_id` = 3007,
    `free` = 0, `price` = 12900, `difficulty` = 2, `train_part` = '胸肩', `calorie_burn` = 360
WHERE `id` = 4;
UPDATE `training`
SET `name` = '7天暴汗燃脂挑战', `first_cate_id` = 1001, `second_cate_id` = 2001, `third_cate_id` = 3003,
    `free` = 0, `price` = 19900, `difficulty` = 3, `train_part` = '全身', `calorie_burn` = 520
WHERE `id` = 5;
UPDATE `training`
SET `name` = '居家全身循环训练', `first_cate_id` = 1001, `second_cate_id` = 2003, `third_cate_id` = 3381,
    `free` = 0, `price` = 15900, `difficulty` = 2, `train_part` = '全身', `calorie_burn` = 400
WHERE `id` = 6;
UPDATE `training`
SET `name` = '核心稳定训练营', `first_cate_id` = 1001, `second_cate_id` = 2054, `third_cate_id` = 3395,
    `free` = 0, `price` = 8900, `difficulty` = 2, `train_part` = '核心', `calorie_burn` = 240
WHERE `id` = 7;
UPDATE `training`
SET `name` = '柔韧拉伸恢复课', `first_cate_id` = 1001, `second_cate_id` = 2004, `third_cate_id` = 3381,
    `free` = 0, `price` = 6900, `difficulty` = 1, `train_part` = '拉伸', `calorie_burn` = 120
WHERE `id` = 8;
UPDATE `training`
SET `name` = '体能冲刺训练', `first_cate_id` = 1001, `second_cate_id` = 2054, `third_cate_id` = 3395,
    `free` = 0, `price` = 14900, `difficulty` = 3, `train_part` = '全身', `calorie_burn` = 450
WHERE `id` = 9;
UPDATE `training`
SET `name` = '新手跑姿纠正课', `first_cate_id` = 1001, `second_cate_id` = 2005, `third_cate_id` = 3381,
    `free` = 0, `price` = 5900, `difficulty` = 1, `train_part` = '全身', `calorie_burn` = 200
WHERE `id` = 10;
UPDATE `training`
SET `name` = '基础燃脂训练营', `first_cate_id` = 1001, `second_cate_id` = 2001, `third_cate_id` = 3003,
    `free` = 1, `price` = 0, `difficulty` = 1, `train_part` = '全身', `calorie_burn` = 220
WHERE `id` = 1549025085494521857;
UPDATE `training`
SET `name` = '21天居家塑形营', `first_cate_id` = 1001, `second_cate_id` = 2002, `third_cate_id` = 3007,
    `difficulty` = 2, `train_part` = '全身', `calorie_burn` = 380
WHERE `id` = 1552558707325374467;
UPDATE `training`
SET `name` = '核心激活训练营', `first_cate_id` = 1001, `second_cate_id` = 2054, `third_cate_id` = 3395,
    `difficulty` = 2, `train_part` = '核心', `calorie_burn` = 260
WHERE `id` = 1589888774267072513;
UPDATE `training`
SET `name` = '综合体能强化营', `first_cate_id` = 1001, `second_cate_id` = 2054, `third_cate_id` = 3395,
    `difficulty` = 3, `train_part` = '全身', `calorie_burn` = 480
WHERE `id` = 1589905661084430337;

UPDATE `training_content`
SET `training_introduce` = '从热身、心率拉升到燃脂收操，帮助新手快速建立训练节奏。',
    `use_people` = '想通过短时高效训练提升基础代谢的新手用户',
    `training_detail` = '训练包含热身、HIIT循环、核心激活与拉伸恢复，适合居家跟练。'
WHERE `id` = 1;
UPDATE `training_content`
SET `training_introduce` = '围绕核心稳定和呼吸控制设计，适合久坐人群唤醒身体。',
    `use_people` = '久坐办公、训练基础薄弱、想先建立核心发力感的人群',
    `training_detail` = '训练以低冲击动作为主，帮助会员建立腹部发力和骨盆稳定能力。'
WHERE `id` = 2;
UPDATE `training_content`
SET `training_introduce` = '以臀腿发力链路为核心，兼顾下肢力量和线条塑形。',
    `use_people` = '想改善臀腿线条、增强下肢力量的进阶用户',
    `training_detail` = '训练包含臀桥、深蹲、箭步蹲等动作组合，并配有阶段性跟练建议。'
WHERE `id` = 3;
UPDATE `training_content`
SET `training_introduce` = '重点强化胸肩推举、稳定和上肢控制能力。',
    `use_people` = '希望改善圆肩、提升上肢力量和训练表现的人群',
    `training_detail` = '训练围绕胸肩推、支撑稳定和弹力带训练展开，适合家庭场景。'
WHERE `id` = 4;
UPDATE `training_content`
SET `training_introduce` = '通过连续高密度训练提升心肺和燃脂效率。',
    `use_people` = '有一定训练基础、希望短期提升状态的会员用户',
    `training_detail` = '7天挑战包含每日任务拆解、打卡建议和恢复提醒，适合作为阶段冲刺。'
WHERE `id` = 5;
UPDATE `training_content`
SET `training_introduce` = '用较少器械完成完整全身循环训练，兼顾心肺与力量。',
    `use_people` = '想在家完成系统训练、控制时长又追求效果的人群',
    `training_detail` = '训练将上肢、下肢、核心动作串联为完整循环，适合固定周计划安排。'
WHERE `id` = 6;
UPDATE `training_content`
SET `training_introduce` = '通过抗旋转、稳定支撑和核心控制提升整体运动表现。',
    `use_people` = '想提高核心稳定、改善跑步和力量训练表现的人群',
    `training_detail` = '训练包含平板支撑变式、抗旋转训练和脊柱稳定练习。'
WHERE `id` = 7;
UPDATE `training_content`
SET `training_introduce` = '帮助训练后恢复肌肉张力，提升柔韧性和身体舒展感。',
    `use_people` = '有训练习惯、需要恢复放松和拉伸整理的人群',
    `training_detail` = '训练围绕肩颈、髋部、腘绳肌和腰背舒展展开，适合训练后单独完成。'
WHERE `id` = 8;
UPDATE `training_content`
SET `training_introduce` = '结合爆发、耐力和节奏控制，帮助完成体能阶段冲刺。',
    `use_people` = '希望提升综合体能、准备阶段挑战任务的进阶会员',
    `training_detail` = '训练围绕间歇跑、波比跳、壶铃摆动等动作设计冲刺单元。'
WHERE `id` = 9;
UPDATE `training_content`
SET `training_introduce` = '从步频、落地到摆臂，帮助新手建立更稳定的跑步动作。',
    `use_people` = '刚开始跑步、想减少动作代偿和提升舒适度的人群',
    `training_detail` = '训练包含跑姿讲解、动作纠正示范和跑前激活练习。'
WHERE `id` = 10;

