<!--训练目录-->
<template>
  <div class="contentBox">
    <!-- 新增 -->
    <div class="subHead">
      <AddButton @handleAdd="handleAdd" :text="text"></AddButton>
    </div>
    <!-- end -->
    <div class="trainingList">
      <el-collapse accordion v-model="activeNames">
        <el-collapse-item v-for="(item, index) in itemData" :key="index">
          <template v-slot:title>
            <div class="titText">
              <span class="icon" v-if="item.sessions.length > 0"></span>
              <div class="textL padl">
                <span
                  ><span v-if="index + 1 > 9">{{ index + 1 }}</span
                  ><span v-else>{{ "0" + (index + 1) }}</span></span
                >
                <span v-if="!item.isEdit" @click.stop="handleEdit(item, i)"
                  >{{ item.name }}<i class="editIcon"></i
                ></span>
                <el-input
                  v-else
                  v-model="item.name"
                  clearable
                  placeholder="请输入"
                  @input="handleTextInput(item)"
                  @blur="handleBlur(item, i)"
                  @focus="handleFocusInput(item)"
                ></el-input
                ><span v-if="item.name === ''" class="tipError"
                  >章名称为空，请输入章名称</span
                >
              </div>
              <div class="textR">
                <span
                  class="textDefault"
                  :class="!item.canUpdate ? 'textForbidden' : ''"
                  @click.stop="handleSortOpen(item)"
                  >章排序</span
                ><span class="textDefault" @click.stop="handleAddMinutia(item)"
                  >添加小节</span
                ><span
                  class="textWarning"
                  :class="!item.canUpdate ? 'textForbidden' : ''"
                  @click.stop="handleDeleteOpen(item)"
                  >删除本章</span
                >
              </div>
            </div>
          </template>
          <div class="itemCon" v-if="item.sessions.length > 0">
            <div class="headTitle">
              <span>序号</span>
              <span class="padl">小节名称</span>
              <span>排序</span>
              <span>操作</span>
            </div>
            <div class="item">
              <ul>
                <li v-for="(val, i) in item.sessions" :key="i">
                  <div class="leftLine"></div>
                  <div class="con">
                    <!-- 序号 -->
                    <div>
                      <span v-if="i + 1 > 9">{{ i + 1 }}</span
                      ><span v-else>{{ "0" + (i + 1) }}</span>
                    </div>
                    <!-- 触发名字编辑 -->

                    <div class="padl">
                      <span v-if="!val.isEdit" @click="handleEdit(val, i)"
                        >{{ val.name }}<i class="editIcon"></i
                      ></span>
                      <el-input
                        :autofocus="i === editActive"
                        placeholder="请输入"
                        v-else
                        v-model="val.name"
                        clearable
                        width="350"
                        @input="handleTextInput(val)"
                        @blur="handleBlur(val, i)"
                        @focus="handleFocusInput(val)"
                      ></el-input
                      ><span v-if="val.name === ''" class="tipError"
                        >小节名称为空，请输入小节名称</span
                      >
                    </div>
                    <div class="sortIcon">
                      <!-- <span class="upforbid" v-if="!val.canUpdate"></span>
                      <span
                        v-else
                        :class="i === 0 ? 'upforbid' : 'up'"
                        @click="handleUp(val, i, 'up')"
                      ></span>
                      <span class="downforbid" v-if="!val.canUpdate"></span>
                      <span
                        v-else
                        :class="
                          i === item.sessions.length - 1 ? 'downforbid' : 'down'
                        "
                        @click="handleDown(val, i, 'down')"
                      ></span> -->
                      <span
                        :class="
                          i === 0 || i <= item.maxSessionIndexOnShelf
                            ? 'upforbid'
                            : 'up'
                        "
                        @click="handleUp(val, i, item)"
                      ></span>
                      <span
                        :class="
                          i === item.sessions.length - 1 ||
                          i < item.maxSessionIndexOnShelf
                            ? 'downforbid'
                            : 'down'
                        "
                        @click="handleDown(item.sessions.length, i, item, val)"
                      ></span>
                    </div>
                    <div
                      class="textWarning"
                      :class="!item.canUpdate ? 'textForbidden' : ''"
                      @click="handleDeleteMinutiaOpen(val)"
                    >
                      删除本节
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="cover"></div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <!-- 删除弹层 -->
    <Delete
      :dialogDeleteVisible="dialogDeleteVisible"
      :deleteText="deleteText"
      @handleDelete="handleDelete"
      @handleClose="handleClose"
    ></Delete>
    <!-- end -->
    <!-- 章排序弹层 -->
    <Sort
      :dialogSortVisible="dialogSortVisible"
      :sortValue="sortValue"
      :minNum="minNum"
      :itemData="itemData"
      @handleClose="handleSortClose"
      @getSortValue="getSortValue"
    ></Sort>
    <!-- end -->
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, nextTick, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
// 接口
import { getTrainingsOutline, baseOutlineSave } from "@/api/program";
// 控制字节数
import { validateTextLength } from "@/utils/index.js";
// 导入组件
// 新增按钮
import AddButton from "@/components/AddButton/index.vue";
// 删除弹层
import Delete from "@/components/Delete/index.vue";
// 章排序
import Sort from "./sort.vue";
// ------定义变量------
// 获取父组件值、方法
const props = defineProps({
  // 训练id
  trainingId: {
    type: Number,
    default: 0,
  },
});
const router = useRouter(); //获取全局
const route = useRoute(); //获取局部
const emit = defineEmits(); //子组件获取父组件事件传值
const text = ref("新增章");
const deleteText = ref(""); //删除提示
// let dialogFormVisible = ref(false); //弹层隐藏显示
let editActive = ref(0); //当前小节的下标值
let isEdit = ref(false); //吃否编辑当前章节的小节
let itemData = ref([]); //目录数据
// let minutiaData = reactive([]); //小节数据
let activeNames = reactive(["1"]);
let dialogDeleteVisible = ref(false); //控制删除弹层
let dialogSortVisible = ref(false); //章排序弹层
let phaseItem = reactive({}); //章内容
let knobData = reactive({}); //节内容
let sortValue = ref(null); //当前的排序值
let phaseId = ref(null); //章节id
let trainingId = props.trainingId !== "null" ? props.trainingId : route.params.id; //训练id
let isName = ref(true); //用来控制在没有填写章节的时候禁止提交表单
let isKnobbleName = ref(true); //用来控制在没有填写小节节的时候禁止提交表单
let minNum = ref(null);
// ------生命周期------
onMounted(() => {
  getOutline();
});
watch("itemData", (newValue, oldValue) => {
  itemData.value = newValue;
});
// ------定义方法------
// 获取目录数据
const getOutline = async () => {
  let params = {
    id: trainingId,
    see: false,
    withPractice: 0, //是否带着练习题，1：带着练习题，0：不带练习题，默认1
  };
  await getTrainingsOutline(params)
    .then((res) => {
      if (res.code === 200) {
        itemData.value = res.data;
        let obj = {
          id: null,
          subId: "010" + 1,
          index: 1,
          name: "章名称",
          trainingId: 1,
          type: 1,
          sessions: [],
          canUpdate: true,
        };
        if (itemData.value.length === 0) {
          itemData.value.push(obj);
        }
      }
    })
    .catch((err) => {});
};
// 1552958577757843500
// 提交
const handleSubmit = async (str) => {
  let courId = props.trainingId !== "null" ? props.trainingId : route.params.id;
  if (itemData.value.length === 0) {
    ElMessage({
      showClose: true,
      message: "未设置章，请设置章",
      type: "error",
      showClose: false,
    });
  } else {
    for (var i = 0; i < itemData.value.length; i++) {
      if (itemData.value[i].name === "") {
        isName.value = false;
        ElMessage({
          showClose: true,
          message: "章名称为空,请输入章名称",
          type: "error",
          showClose: false,
        });
        return false;
      } else {
        isName.value = true;
      }
      for (var j = 0; j < itemData.value[i].sessions.length; j++) {
        if (itemData.value[i].sessions[j].name === "") {
          isKnobbleName.value = false;
          ElMessage({
            showClose: true,
            message: "小节名称为空,请输入小节名称",
            type: "error",
            showClose: false,
          });
          return false;
        } else {
          isKnobbleName.value = true;
        }
      }
      itemData.value[i].index = i + 1; //重新给数组排序
    }
    if (isName.value && isKnobbleName.value) {
      let params = {
        datas: itemData.value,
        id: courId,
        step: 2,
      };
      await baseOutlineSave(params)
        .then((res) => {
          if (res.code === 200) {
            emit("getActive", 2);
            // 保存并返回
            if (str === "getback") {
              router.push({
                path: "/program/index",
              });
            } else {
              // 保存并下一步
              router.push({
                path: `/program/add/` + courId,
              });
            }
          } else {
            ElMessage({
              showClose: true,
              message: res.data.msg,
              type: "error",
              showClose: false,
            });
          }
        })
        .catch((err) => {});
    }
  }
};
// 打开章排序弹层
const handleSortOpen = (item) => {
  if (item.canUpdate) {
    sortValue.value = item.index;
    minNum.value = item.maxIndexOnShelf + 1;
    if (item.id) {
      phaseId = item.id; //后传传的id
    } else {
      phaseId = item.subId; //获取章的前端自定义id
    }

    dialogSortVisible.value = true;
  }
};
// 关闭章排序弹层
const handleSortClose = () => {
  dialogSortVisible.value = false;
};
// 删除章内容
const handleDelete = async () => {
  let knob = knobData.value; //要删除的小节
  // 删除小节逻辑
  if (knob !== undefined && (knob.id || knob.subId)) {
    // 遍历章节
    itemData.value.map((val, index) => {
      val.sessions.map((obj, i) => {
        if (
          (obj.id === knob.id && obj.id && knob.id) ||
          (obj.subId === knob.subId && obj.subId && knob.subId)
        ) {
          val.sessions.splice(i, 1);
          ElMessage({
            showClose: true,
            message: "删除成功",
            type: "success",
            showClose: false,
          });
        }
      });
    });
  } else {
    // 章删除逻辑
    let phase = phaseItem.value; //临时要删除的章
    itemData.value.map((val, index) => {
      // 没有保存数据之前前端需要自定义一个subId，用自定义的id来判断要删除的数据
      // id不能为空的情况下删除章
      if (
        (val.subId === phase.subId && val.subId && phase.subId) ||
        (val.id === phase.id && val.id && phase.id)
      ) {
        itemData.value.splice(index, 1);
        ElMessage({
          showClose: true,
          message: "删除成功",
          type: "success",
          showClose: false,
        });
      }
    });
  }
  // 关闭弹层
  handleClose();
};
// 删除改章，判断改章节是否有小节，如果有，显示删除弹层，如果没有，直接删除改章
const handleDeleteOpen = (item) => {
  if (item.canUpdate) {
    phaseItem.value = item;
    // 遍历章节
    itemData.value.map((val, index) => {
      // 没有保存数据之前前端需要自定义一个subId
      if (
        val.subId === item.subId ||
        (val.id === item.id && val.id && item.id)
      ) {
        if (item.sessions.length > 0) {
          dialogDeleteVisible.value = true;
          deleteText.value = `该章节下包含${item.sessions.length}个小节，是否继续删除？`;
        } else {
          itemData.value.splice(index, 1);
          ElMessage({
            showClose: true,
            message: "删除成功",
            type: "success",
            showClose: false,
          });
        }
      }
    });
  }
};
// 打开删除小节弹层
const handleDeleteMinutiaOpen = (item) => {
  if (item.canUpdate) {
    if (item.id || item.subId) {
      knobData.value = item;
      dialogDeleteVisible.value = true;
      deleteText.value = `若删除该小节，阶段三、四中的小节将一同删除，是否继续删除？`;
    }
  }
};
// 添加小节
const handleAddMinutia = (item) => {
  itemData.value.map((val) => {
    //此处有两种情况，第一种是从后端获取的数据有id，第二种是没保存数据的时候前端添加数据自定义的subId
    if (
      (val.id && val.id === item.id) ||
      (val.subId && val.subId === item.subId)
    ) {
      let obj = {};
      obj = {
        id: null,
        subId: val.sessions.length + 1, //给小节添加临时id
        index: null,
        name: "小节名称",
        isEdit: false,
        type: 2,
        canUpdate: true,
      };
      val.sessions.push(obj);
    }
  });
};
// 编辑章、小节
const handleEdit = (item, i) => {
  item.isEdit = true;
  // item.isEdit = true
  editActive.value = i;
};
const handleBlur = (item, i) => {
  if (item.name !== "") {
    item.isEdit = false;
  }

  // item.isEdit =false
  // editActive.value = i;
};
// 打开新增弹层
const handleAdd = () => {
  let obj = {};
  let num = 0;
  if (itemData.value === undefined) {
    num = 1;
  } else {
    num = itemData.value.length + 1;
  }
  obj = {
    id: null,
    subId: "010" + num,
    index: num,
    name: "章名称",
    trainingId: 1,
    type: 1,
    sessions: [],
    canUpdate: true,
  };

  // itemData.push(JSON.parse(JSON.stringify(obj)))
  itemData.value.push(obj);
};
// 关闭删除弹层
const handleClose = () => {
  dialogDeleteVisible.value = false;
};
//获取设置的配许值
const getSortValue = (num) => {
  itemData.value.forEach((val, index) => {
    if (val.id === phaseId || val.subId === phaseId) {
      // 此处减1，原因是num是从1开始的，数组索引值是从0开始的
      let obj = val; //先把当前的要排序的对象用变量保存，然后删除当前触发的对象
      itemData.value.splice(index, 1);
      itemData.value.splice(num - 1, 0, obj);
    }
  });
  console.log(itemData.value);
};
//
const handleTextInput = (obj) => {
  console.log(obj);
  const value = validateTextLength(obj.name, 30);
  obj.name = value.val;
};
//
const handleFocusInput = (obj) => {
  console.log(obj);
  if (obj.name === "章名称") {
    obj.name = "";
  }
  if (obj.name === "小节名称") {
    obj.name = "";
  }
};
// 排序向上
const handleUp = (val, index, item) => {
  if (index <= item.maxSessionIndexOnShelf) {
    return false;
  } else {
    itemData.value.forEach((obj) => {
      if (obj.id === item.id) {
        if (index != 0) {
          obj.sessions[index] = obj.sessions.splice(
            index - 1,
            1,
            obj.sessions[index]
          )[0];
        } else {
          obj.sessions.push(obj.sessions.shift());
        }
      }
    });
  }
};
// 排序向下
const handleDown = (length, index, item, val) => {
  if (index === length - 1 || index < item.maxSessionIndexOnShelf) {
    return false;
  } else {
    
    itemData.value.forEach((obj) => {
      if (obj.id === item.id) {
        if (index != obj.sessions.length - 1) {
          obj.sessions[index] = obj.sessions.splice(
            index + 1,
            1,
            obj.sessions[index]
          )[0];
        } else {
          obj.sessions.unshift(obj.sessions.splice(index, 1)[0]);
        }
        obj.sessions.forEach((val) => {});
      }
    });
  }
};
// 向父组件暴露方法
defineExpose({
  handleSubmit,
});
</script>
<style lang="scss" scoped>
:deep(.trainingList .el-collapse-item__header) {
  min-width: 1040px;
}
.detailBox .trainingList .el-collapse-item .el-input {
  width: 278px;
}
.detailBox .el-input .el-cascader,
.detailBox .el-input {
  width: 278px;
}
.detailBox .trainingList .el-collapse-item .el-input {
  min-width: 278px;
}
// .trainingList .itemCon .item li .con > div:nth-child(3){
//   width: 15%;
//   margin-right: 75px;
// }
</style>

