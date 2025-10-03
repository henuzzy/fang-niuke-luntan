<template>
  <div class="message-detail">
    <div class="toolbar">
      <el-button size="small" @click="$router.back()">返回</el-button>
      <div class="spacer"></div>
      <el-button size="small" type="primary" @click="dialogVisible=true">给TA私信</el-button>
    </div>

    <el-card class="list-card">
      <el-empty v-if="letters.length===0" description="暂无聊天记录" />
      <div v-else class="letters">
        <div v-for="item in letters" :key="item.id" class="letter-item">
          <el-avatar :size="36" :src="avatarOf(item.fromId)" />
          <div class="bubble">
            <div class="meta">
              <span class="name">{{ nameOf(item.fromId) }}</span>
              <span class="time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="content">{{ item.content }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="给TA私信" width="420px">
      <el-input v-model="content" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="输入发送内容" />
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="send">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLetters, sendMessage } from '@/api/message'

const route = useRoute()
const conversationId = route.params.conversationId
const letters = ref([])
const dialogVisible = ref(false)
const content = ref('')

function parsePeerId(cid) {
  if (!cid) return null
  const parts = String(cid).split('_').map(v => Number(v))
  const me = Number(localStorage.getItem('userId'))
  if (parts.length !== 2 || !me) return null
  return parts[0] === me ? parts[1] : parts[0]
}

const peerId = parsePeerId(conversationId)

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function nameOf(uid) {
  const me = Number(localStorage.getItem('userId'))
  if (uid === me) return '我'
  // 简化：显示对方ID，后续可请求用户昵称
  return `用户${uid}`
}

function avatarOf(uid) {
  const me = Number(localStorage.getItem('userId'))
  if (uid === me) return localStorage.getItem('userAvatar') || ''
  return ''
}

async function load() {
  const res = await getLetters({ conversationId, page: 1, pageSize: 50 })
  const data = res?.data?.data || res?.data || []
  letters.value = Array.isArray(data) ? data.reverse() : []
}

async function send() {
  const fromId = Number(localStorage.getItem('userId'))
  const toId = Number(peerId)
  const c = (content.value || '').trim()
  if (!fromId || !toId) return ElMessage.warning('登录信息异常')
  if (!c) return ElMessage.warning('请输入内容')
  await sendMessage({ fromId, toId, content: c })
  ElMessage.success('发送成功')
  dialogVisible.value = false
  content.value = ''
  load()
}

onMounted(load)
</script>

<style scoped>
.message-detail { padding: 0; }
.toolbar { display: flex; align-items: center; margin-bottom: 10px; }
.spacer { flex: 1; }
.list-card { padding: 0 0 8px; }
.letters { padding: 10px 12px; }
.letter-item { display: flex; gap: 10px; padding: 10px 6px; }
.bubble { background: #fff; border: 1px solid #f0f0f0; border-radius: 6px; padding: 8px 12px; flex: 1; }
.meta { display: flex; justify-content: space-between; color: #909399; font-size: 12px; margin-bottom: 6px; }
.content { white-space: pre-wrap; line-height: 1.6; }
</style>


