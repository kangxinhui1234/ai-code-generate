<template>
  <h2 class="title">代码生成助手 -- 用户注册</h2>
  <div class="desc">一分钟完成注册，立即开始生成应用</div>
  <a-form :model="formState" name="normal_register" class="register-form" @finish="onFinish">
    <a-form-item
      label="用&nbsp;&nbsp;户&nbsp;&nbsp;名"
      name="userAccount"
      :rules="[{ required: true, message: '请输入用户名' }]"
    >
      <a-input v-model:value="formState.userAccount">
        <template #prefix>
          <UserOutlined class="site-form-item-icon" />
        </template>
      </a-input>
    </a-form-item>

    <a-form-item label="密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码" name="userPassword" :rules="passwordRules">
      <a-input-password v-model:value="formState.userPassword">
        <template #prefix>
          <LockOutlined class="site-form-item-icon" />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item label="确认密码" name="checkPassword" :rules="confirmPasswordRules">
      <a-input-password v-model:value="formState.checkPassword">
        <template #prefix>
          <LockOutlined class="site-form-item-icon" />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item>
      <span>已有账号？</span>
      <a class="login-form-forgot" href="/user/login">去登录</a>
    </a-form-item>

    <a-form-item>
      <a-button :disabled="disabled" type="primary" html-type="submit" class="register-form-button">
        注册
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { userRegister } from '@/api/userController'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const passwordRules: Rule[] = [
  { required: true, message: '请输入密码' },
  { min: 8, message: '密码长度不能小于8位' },
]

const confirmPasswordRules: Rule[] = [
  { required: true, message: '请再次输入密码' },
  {
    validator: async (_rule, value) => {
      if (!value) return Promise.reject('请再次输入密码')
      if (value !== formState.userPassword) return Promise.reject('两次输入的密码不一致')
      return Promise.resolve()
    },
    trigger: 'change',
  },
]

const onFinish = async (values: API.UserRegisterRequest) => {
  const res = await userRegister(values)
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功，请登录')
    router.push({ path: '/user/login', replace: true })
  } else {
    message.error('注册失败，' + res.data.msg)
  }
}

const disabled = computed(() => {
  return !(formState.userAccount && formState.userPassword && formState.checkPassword)
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

.register-form-button {
  width: 100%;
}
</style>
