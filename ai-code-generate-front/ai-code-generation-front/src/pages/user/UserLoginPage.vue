<template>
  <h2 class="title">代码生成助手 -- 用户登录</h2>
  <div class="desc">不写一行代码 生成完整应用</div>
  <a-form :model="formState" name="normal_login" class="login-form" @finish="onFinish">
    <a-form-item
      label="用户名"
      name="userAccount"
      :rules="[{ required: true, message: '请输入用户名' }]"
    >
      <a-input v-model:value="formState.userAccount">
        <template #prefix>
          <UserOutlined class="site-form-item-icon" />
        </template>
      </a-input>
    </a-form-item>

    <a-form-item
      label="密&nbsp; &nbsp;  码"
      name="userPassword"
      :rules="[
        { required: true, message: '请输入密码' },
        { min: 8, message: '密码长度不能小于8位' },
      ]"
    >
      <a-input-password v-model:value="formState.userPassword">
        <template #prefix>
          <LockOutlined class="site-form-item-icon" />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item>
      <!--      <a-form-item name="remember" no-style>-->
      <!--        <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>-->
      <!--      </a-form-item>-->
      <span>没有账号？</span>
      <a class="login-form-forgot" href="/user/register">去注册</a>
    </a-form-item>

    <a-form-item>
      <a-button :disabled="disabled" type="primary" html-type="submit" class="login-form-button">
        Log in
      </a-button>
      Or
      <a href="">register now!</a>
    </a-form-item>
  </a-form>
</template>
<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogin } from '@/api/userController'

// VUE3响应式变量  会检测到值变化自动更新 符合API的格式
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const onFinish = async (values: API.UserLoginRequest) => {
  const res = await userLogin(values) // 调用api
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    const dataString = JSON.stringify(res.data.data, null, 2)
    console.log(dataString)
    message.success('登录成功:')
    await router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.msg)
  }
}

// const onFinish = values => {
//   // 点击提交
//   console.log('Success:', values);
//
// };
const onFinishFailed = (errorInfo) => {
  console.log('Failed:', errorInfo)
}
const disabled = computed(() => {
  return !(formState.userAccount && formState.userPassword)
})
</script>
<style scoped>
.title {
  margin: 20px;
}

.desc {
  color: darkgray;
  text-align: center;
  margin-bottom: 20px;
}
#components-form-demo-normal-login .login-form {
  max-width: 300px;
}
#components-form-demo-normal-login .login-form-forgot {
  float: right;
}
#components-form-demo-normal-login .login-form-button {
  width: 100%;
}
</style>
