<!-- 训练管理搜索 -->
<template>
  <div class="bg-wt radius marg-tp-20">
    <div class="pad-30 searchForm">
      <el-form ref="ruleForm" :inline="true" :model="searchData">
        <el-row :gutter="30">
          <el-col :span="6">
            <el-form-item label="训练分类" prop="categoryIdLv3">
              <div class="el-input">
                <el-cascader
                  v-model="searchData.categoryIdLv3"
                  :options="typeData.value"
                  @change="handleChange"
                  :props="{
                    label: 'name',
                    value: 'id',
                    children: 'children',
                  }"
                  clearable
                  style="width: 100%"
                ></el-cascader>
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="收费方式" prop="free">
              <div class="el-input">
                <el-select
                  v-model="searchData.free"
                  clearable
                  placeholder="请选择"
                  style="width: 100%"
                  @change="handleFree"
                >
                  <el-option
                    v-for="item in sellingModelData"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="训练名称" prop="keyword">
              <el-input placeholder="请输入训练名称" clearable v-model="searchData.keyword" />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="训练难度" prop="difficulty">
              <div class="el-input">
                <el-select
                  v-model="searchData.difficulty"
                  clearable
                  placeholder="请选择"
                  style="width: 100%"
                  @change="handleDifficulty"
                >
                  <el-option
                    v-for="item in trainingDifficultyData"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="训练部位" prop="trainPart">
              <div class="el-input">
                <el-select
                  v-model="searchData.trainPart"
                  clearable
                  placeholder="请选择"
                  style="width: 100%"
                  @change="handleTrainPart"
                >
                  <el-option
                    v-for="item in trainingPartData"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="更新时间" prop="datePicker">
              <div class="el-input">
                <el-date-picker
                  v-model="datePicker"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  clearable
                  align="right"
                  @change="handleDate($event)"
                ></el-date-picker>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="30">
          <el-col :span="24">
            <div class="btn programBtn">
              <el-button class="button primary" @click="handleSearch">搜索</el-button>
              <el-button class="button buttonSub" @click="handleReset(ruleForm)">重置</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { sellingModelData, trainingDifficultyData } from "@/utils/commonData"
import { getTypeAll } from "@/api/api"

defineProps({
  searchData: {
    type: Object,
    default: () => ({}),
  },
})

const trainingPartData = [
  { value: "全身", label: "全身" },
  { value: "臀腿", label: "臀腿" },
  { value: "胸肩", label: "胸肩" },
  { value: "核心", label: "核心" },
  { value: "拉伸", label: "拉伸" },
]

const emit = defineEmits()
const ruleForm = ref()
let typeData = reactive([])
let datePicker = ref([])

onMounted(() => {
  getTypeList()
})

const getTypeList = async () => {
  await getTypeAll({ admin: true })
    .then((res) => {
      if (res.code === 200) {
        typeData.value = res.data
      }
    })
    .catch(() => {})
}

const handleSearch = () => {
  emit("handleSearch")
}

const handleReset = (ruleForm) => {
  ruleForm.resetFields()
  emit("handleReset")
  emit("getList")
}

const handleChange = (value) => {
  emit("getTypeData", value)
}

const handleDate = (val) => {
  emit("getTime", val)
}

const handleFree = (val) => {
  emit("getFree", val)
}

const handleDifficulty = (val) => {
  emit("getDifficulty", val)
}

const handleTrainPart = (val) => {
  emit("getTrainPart", val)
}
</script>

<style lang="scss">
.el-picker__popper.el-popper {}

.el-picker-panel__icon-btn:hover {
  color: #ff734f;
}

.el-date-picker__header-label:hover {
  color: #ffffff;
}

.el-date-table td.today {
  .el-date-table-cell__text {
    color: #ff734f;
  }
}

.el-picker-panel .el-date-table td.available:hover {
  color: #ffffff;

  .el-date-table-cell {
    .el-date-table-cell__text {
      background-color: #ff734f;
    }
  }
}

.el-date-table td.start-date {
  .el-date-table-cell__text {
    background-color: #ff734f;
  }
}

.el-date-table td.end-date {
  .el-date-table-cell__text {
    background-color: #ff734f;
  }
}

.el-date-table td.in-range {
  .el-date-table-cell {
    background-color: #faf4ee;

    &:hover {
      background-color: #faf4ee;
    }
  }
}

.button {
  font-family: PingFangSC-Regular;
}

:deep(.el-input .el-input__wrapper .el-input__inner) {
  color: #332929;
}
</style>

<style lang="scss" scoped>
:deep(.el-input__wrapper) {
  .el-input__inner {
    &::placeholder {
      color: #b5abab;
    }
  }
}

.programBtn {
  padding: 10px 0 20px;
}
</style>
