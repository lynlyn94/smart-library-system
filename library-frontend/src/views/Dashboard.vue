<template>
  <div>
    <h1>系统概览</h1>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-statistic title="在馆图书" :value="1250" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="今日借出" :value="42" />
      </el-col>
    </el-row>
  </div>
</template>
