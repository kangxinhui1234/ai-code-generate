<template>
  <div style="margin-bottom: 20px">
    <a-form layout="inline" :model="searchParams" name="normal_login" class="login-form" @finish="doSearch">
      <a-form-item
        label="用户账号"
        name="userAccount"
        :rules="[{ required: false, message: '请输入用户账号' }]"
      >
        <a-input v-model:value="searchParams.userAccount">
          <template #prefix>
            <UserOutlined class="site-form-item-icon" />
          </template>
        </a-input>
      </a-form-item>

      <a-form-item
        label="用户名"
        name="userName"
        :rules="[
        { required: false, message: '请输入用户名' }
      ]"
      >
        <a-input-password v-model:value="searchParams.userName">
          <template #prefix>
            <LockOutlined class="site-form-item-icon" />
          </template>
        </a-input-password>
      </a-form-item>


      <a-form-item>
        <a-button :disabled="disabled" type="primary" html-type="submit"  class="login-form-button">
          Search
        </a-button>
      </a-form-item>
    </a-form>
  </div>

  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange">
    <template #headerCell="{ column }">
      <template v-if="column.key === 'name'">
        <span>
          <smile-outlined />
          Name
        </span>
      </template>
    </template>

    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'userAvatar'">
        <a-image :src="record.userAvatar" :width="120" />
      </template>
      <template v-else-if="column.dataIndex === 'userRole'">
        <div v-if="record.userRole === 'admin'">
          <a-tag color="green">管理员</a-tag>
        </div>
        <div v-else>
          <a-tag color="blue">普通用户</a-tag>
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'createTime'">
        {{


          dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss')
        }}
      </template>
      <template v-else-if="column.key === 'action'">
        <a-button danger @click = "doDelete(record.id)">删除</a-button>
      </template>
    </template>

  </a-table>
</template>
<script lang="ts" setup>
import dayjs from "dayjs";
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import {deleteUser, listUserVoByPage} from "@/api/userController";
import {computed, onMounted, reactive, ref} from "vue";
import {message} from "ant-design-vue";
const columns = [
    {
      title: 'id',
      dataIndex: 'id',
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
    },
    {
      title: '用户名',
      dataIndex: 'userName',
    },
    {
      title: '头像',
      dataIndex: 'userAvatar',
    },
    {
      title: '简介',
      dataIndex: 'userProfile',
    },
    {
      title: '用户角色',
      dataIndex: 'userRole',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
    },
    {
      title: '操作',
      key: 'action',
    },
  ]
;

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 1,
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 1,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () =>{
  searchParams.pageNum = 1
  fetchData();
}


// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})


// 删除数据
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
// 在 deleteUser 函数中添加日志
  console.log('Sending request with data:', id);
  const res = await deleteUser({
    id
    })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}



</script>

<style>
#userManagePage {
  width: 100%;
}
</style>

