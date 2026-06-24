<template>
  <el-form
    :model="coacheditData"
    ref="ruleFormRef"
    :rules="rules"
    label-width="130px"
    class="demo-ruleForm"
  >
    <el-form-item label="教练名称：" prop="name">
      <div class="el-input">
        <span>{{ coachData.name }}</span>
      </div>
    </el-form-item>
    <el-form-item label="头像：" class="el-form-icon">
      <div class="el-input-icon">
        <!-- <UploadImage
          @getFlag="getFlag"
          @getCoverUrl="getCoverUrl"
          @setUplad="setUplad"
          :upladImg="photoImg == '' ? photoImg : coachData.icon"
          :isTraining="isTraining"
          ref="uploadImg"
        ></UploadImage> -->
        <img :src="coachData.icon" alt="" srcset="" class="icon" />
      </div>
    </el-form-item>
    <el-form-item label="教练手机号：" prop="cellPhone">
      <div class="el-input">
        <span>{{ coachData.cellPhone }}</span>
      </div>
    </el-form-item>
    <el-form-item label="岗位：" prop="job">
      <div class="el-input">
        <el-input
          v-model="coacheditData.job"
          @input="jobTextInput"
          show-word-limit
          class="cursour"
          width="369"
          placeholder="请输入"
        ></el-input>
        <span class="numText" :class="jobNumVal === 0 ? 'tip' : ''"
          >{{ jobNumVal }}/200</span
        >
      </div>
    </el-form-item>
    <el-form-item label="教练介绍：" prop="intro">
      <div class="el-input">
        <el-input
          v-model="coacheditData.intro"
          type="textarea"
          placeholder="请输入"
          resize="none"
          @input="introTextInput"
          show-word-limit
        ></el-input>
        <span class="numText" :class="introNumVal === 0 ? 'tip' : ''"
          >{{ introNumVal }}/200</span
        >
      </div>
    </el-form-item>
    <el-form-item label="教练形象照：" prop="photo">
      <div class="tearchUpload">
        <div class="el-input">
          <UploadImage
            @getFlag="getFlag"
            @getCoverUrl="getCoverUrl"
            @setUplad="setUplad"
            :upladImg="photoImg !== '' ? photoImg : coacheditData.photo"
            ref="uploadImg"
            @change="editCoach"
          ></UploadImage>
        </div>
      </div>
    </el-form-item>
    <el-form-item label="密码：" class="passwordbody">
      <div class="passwordbody el-input">
        <el-input
          type="password"
          v-model="coachData.cellPhone"
          placeholder="请输入"
          maxlength="20"
          disabled
        ></el-input>
      </div>

      <a
        href="###"
        onclick="return false;"
        style="color: #2080f7; margin-left: 20px"
        @click="editpassword"
        >修改密码</a
      >
    </el-form-item>
    <el-form-item label="旧密码：" v-show="ispassword" prop="oldPassword">
      <div class="el-input">
        <el-input
          type="password"
          v-model="coacheditData.oldPassword"
          placeholder="请输入原密码"
          @input="editCoach"
          @blur="checkoldPassword"
        ></el-input>
      </div>
    </el-form-item>
    <el-form-item label="新密码：" v-show="ispassword" prop="password">
      <div class="el-input">
        <el-input
          type="password"
          v-model="coacheditData.password"
          placeholder="请输入新密码"
          @input="editCoach"
        ></el-input>
      </div>
    </el-form-item>
    <el-form-item label="确认新密码：" v-show="ispassword" prop="checkpassword">
      <div class="el-input">
        <el-input
          type="password"
          v-model="coacheditData.checkpassword"
          placeholder="请再次输入新密码"
          @input="editCoach"
        ></el-input>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
// 引用
import { ref, reactive, nextTick, watch, computed } from "vue";
// 引入接口
import { CheckPassword } from "@/api/staffs.js"; // 验证密码
// 控制字节数
import { validateTextLength } from "@/utils/index.js";
// 引用上传图片组件
import UploadImage from "@/components/UploadImage/index.vue";
// 创建一个布尔值，控制密码修改的显示隐藏
const ispassword = ref(false);
let photoImg = ref(""); //图片路径
let flag = ref(false); //用来控制上传图片是否校验成功
let isPassword = ref(); //用来检测密码是否重复
let introNumVal = ref(0); //教练介绍字数显示
let jobNumVal = ref(0); //岗位字数显示
// 表单校验ref
const ruleFormRef = ref(); //表单校验ref
// 定义emit方法，用于触发父组件中的方法
const emit = defineEmits();
// 引入父级传参
const props = defineProps({
  coachData: {
    type: Object,
    default: {},
  },
});
// 创建数组，其中内容为输入框中的内容
const coacheditData = reactive({
  // job的值为coachData.job
  job: "",
  intro: "",
  photo: "",
  oldPassword: "",
  password: "",
  checkpassword: "",
});
// 对输入框内的内容进行重新拼接，使其符合接口要求
const coachseditData = reactive({
  // job的值为coachData.job
  job: "",
  intro: "",
  photo: "",
  oldPassword: "",
  password: "",
});
// 监听输入框中的内容，当内容发生改变时，重新赋值给数组
watch(coacheditData, (news, olds) => {
  coachseditData.oldPassword = coacheditData.oldPassword;
  coachseditData.password = coacheditData.password;
  coachseditData.job = coacheditData.job;
  coachseditData.intro = coacheditData.intro;
  coachseditData.photo = coacheditData.photo;
});
// 将教练介绍，教练形象照和岗位的值赋值给coacheditData用于传输修改内容
watch(props, (news, olds) => {
  coacheditData.job = props.coachData.job;
  coacheditData.intro = props.coachData.intro;
  coacheditData.photo = props.coachData.photo;
  // 教练介绍默认的字数
  const introValue = validateTextLength(props.coachData.intro, 200);
  introNumVal.value = introValue.numVal;
  // 岗位默认的字数
  const jobValue = validateTextLength(props.coachData.job, 20);
  jobNumVal.value = jobValue.numVal;
});
// 监听photoImg的值，当值发生改变时，将值赋值给coacheditData.photo
watch(photoImg, (news, olds) => {
  coacheditData.photo = photoImg.value;
});
// 通过改变布尔值的取值，实现点击修改密码出现修改密码框，再次点击隐藏修改密码框
const editpassword = () => {
  ispassword.value = !ispassword.value;
};
// 为密码框添加正则验证
const rules = ref(() => {
 return setRules()
});
// 将rules动态配置 避免使用computed的时候 更改rules之后自动效验 导致打开下拉就效验爆红的Bug
const setRules = () => {
  const baseRule = {
    job: [
      {
        required: true,
        message: "岗位为空，请输入岗位名称",
        trigger: "blur",
      },
      { min: 2, max: 20, message: "请至少输入2个字", trigger: "blur" },
      {
        pattern: /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/,
        message: "岗位名称格式错误，请输入中英文、数字",
        trigger: "blur",
      },
    ],
    intro: [
      {
        required: true,
        message: "教练介绍为空，请输入教练介绍",
        trigger: "blur",
      },
      { min: 10, max: 200, message: "请至少输入10个字符", trigger: "blur" },
    ],
    photo: [
      {
        required: true,
        message: "教练形象照为空，请上传教练形象照",
        trigger: "blur",
      },
    ],
  };
  const passRule = {
    oldPassword: [
      {
        required: true,
        message: "密码为空，请输入密码",
        trigger: "blur",
      },
      // 根据isPassword的值，验证是否与旧密码一致，若isPassword的值为true，则不一致，出错误文本提示：旧密码不正确，请重新输入；
      // 若isPassword的值为false，则一致，不出错误文本提示
      {
        validator: (rule, value, callback) => {
          // 延迟执行0.3s，防止重复校验和校验不准确
          setTimeout(() => {
            if (isPassword.value === false) {
              callback(new Error("旧密码不正确，请重新输入"));
            } else {
              callback();
            }
          }, 300);
        },
        trigger: "blur",
      },
    ],
    password: [
      {
        required: true,
        message: "密码为空，请输入密码",
        trigger: "blur",
      },
      { min: 6, max: 16, message: "密码格式错误，请重新输入", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value == coacheditData.oldPassword) {
            callback(new Error("新密码不能与原密码一致，请重新输入"));
          } else {
            callback();
          }
        },
        trigger: "blur",
      },
    ],
    checkpassword: [
      {
        required: true,
        message: "密码为空，请输入新密码",
        trigger: "blur",
      },
      {
        validator: (rule, value, callback) => {
          if (value !== coacheditData.password) {
            callback(new Error("新密码与确认新密码不一致"));
          } else {
            callback();
          }
        },
        trigger: "blur",
      },
    ],
  };
  // 根据isPassword的值，返回不同的验证规则,若isPassword的值为true，则返回验证密码的规则，若isPassword的值为false，则返回不验证密码的规则,即只验证基本信息,不验证密码
  return ispassword.value ? { ...baseRule, ...passRule } : baseRule;
}
// 校验图片
const getFlag = (val) => {
  flag.value = val;
};
// 获取封面图片
const getCoverUrl = (val) => {
  photoImg.value = val;
  editCoach();
};
// 将图片的地址赋值给coacheditData.photo
const setUplad = (val) => {
  photoImg.value = "";
  coacheditData.photo = "";
};
// 定义事件，用于传递coacheditData的内容给父组件
const editCoach = () => {
  // 将coacheditData中的内容传递给父组件
  emit("editCoach", coachseditData);
};
const introTextInput = () => {
  // 教练介绍控制200个字符
  nextTick(() => {
    const value = validateTextLength(coacheditData.intro, 200);
    coacheditData.intro = value.val;
    introNumVal.value = value.numVal;
  });
  emit("editCoach", coachseditData);
};
// 岗位控制20个字符
const jobTextInput = () => {
  nextTick(() => {
    const value = validateTextLength(coacheditData.job, 20);
    coacheditData.job = value.val;
    jobNumVal.value = value.numVal;
  });
  emit("editCoach", coachseditData);
};
// 定义事件，用于校验当前所有的密码框格式是否正确
const rulespassWord = () => {
  rules.value = setRules()
  // 校验当前所有的密码框格式是否正确
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      emit("handleSaveCoach", valid); // 将校验结果传递给父组件
    } else {
      // 清除密码框中的内容
      coacheditData.oldPassword = "";
      coacheditData.password = "";
      coacheditData.checkpassword = "";
      introNumVal.value = 0;
      return false; // 不执行下面的代码
    }
  });
};
const checkoldPassword = async () => {
  await CheckPassword(coacheditData.oldPassword).then((res) => {
    if (res.code === 200) {
      isPassword.value = res.data.right;
    }
  });
};

// 向父组件宏显式暴露方法
defineExpose({
  rulespassWord, // 校验当前所有的密码框格式是否正确
});
</script>
<style lang="scss" scoped>
:deep(.el-input__count) {
  height: 36%;
  font-weight: 400;
  font-size: 12px;
  color: #b5abab;
}
.el-form-icon {
  position: absolute;
  right: 235px;
  top: 178px;
  .el-input-icon {
    font-weight: 400;
    font-size: 12px;
    color: #000;
    .icon {
      width: 89px !important;
      height: 89px !important;
      border-radius: 50%;
      overflow: hidden;
    }
    :deep(.avatar-uploader) {
      width: 89px !important;
      height: 89px !important;
      border-radius: 50%;
      overflow: hidden;

      .el-upload.el-upload {
        width: 89px !important;
        height: 89px !important;
        // 将图片的边框设置为圆形
        border-radius: 50%;
        overflow: hidden;
      }
    }
    :deep(.Prompttext) {
      display: none;
    }
  }
  :deep(.el-input__wrapper) {
    padding-right: 0;
  }
}
</style>
<style lang="scss">
.cursour {
  .el-input__wrapper {
    padding-right: 11px !important;
  }
}
</style>
<style lang="scss" scoped>
.el-input {
  width: 369px;
}
.el-form-item {
  // margin-bottom: 10px;
}
.password {
  margin-top: 10px;
}
:deep(.tearchUpload){
 .uploadBox{
    font-weight: 400;
    font-size: 12px;
    color: #887E7E;
    .avatar-uploader,.avatar-uploader .el-upload{
      width: 160px;
      height: 160px;
      .el-upload{
        .avatar{
          width: 158px;
          height: 158px;
        }
      }
    }
  } 
}
</style>
<style lang="scss">
.passwordbody {
  label {
    margin-top: 10px;
  }
}
</style>

