<template>
  <div class="messages-page">
    <el-card class="msg-card">
      <div class="msg-header">
        <div class="tabs">
          <a :class="{active: activeTab==='private'}" @click="activeTab='private'">
            朋友私信
            <el-badge :value="unreadTotal" v-if="unreadTotal>0" />
          </a>
          <a :class="{active: activeTab==='notify'}" @click="switchNotify()">
            系统通知
          </a>
        </div>
        <el-button type="primary" size="small" @click="openDialog">发私信</el-button>
      </div>

      <div v-if="activeTab==='private'">
        <el-empty v-if="conversations.length===0" description="暂无私信" />
        <el-scrollbar v-else class="list">
          <div class="conv-item" v-for="item in conversations" :key="item.id">
            <div class="conv-avatar" @click.stop="openProfile(item)">
              <el-avatar :size="40" :src="peerInfo(item).headerUrl">
                <template #default>{{ avatarInitial(peerInfo(item).username) }}</template>
              </el-avatar>
            </div>
            <div class="conv-content">
              <div class="conv-title">{{ peerInfo(item).username || item.conversationId }}</div>
              <div class="conv-snippet" @click="openConversation(item)">{{ item.content }}</div>
            </div>
            <div class="conv-meta">
              <div class="time">{{ formatTime(item.createTime) }}</div>
              <el-badge v-if="item.unread && item.unread>0" :value="item.unread" />
            </div>
          </div>
        </el-scrollbar>
      </div>

      <div v-else class="notice-section">
        <div class="notice-card" v-for="card in noticeCards" :key="card.key">
          <div class="left">
            <el-badge :value="card.unread" v-if="card.unread>0">
              <div class="icon" :class="card.key"></div>
            </el-badge>
            <div class="title">{{ card.title }}</div>
          </div>
          <div class="content">
            <div class="snippet">{{ card.snippet }}</div>
          </div>
          <div class="meta">
            <div class="time">{{ card.time }}</div>
            <a class="link" @click="openNoticeList(card.key)">共 {{ card.total }} 条会话</a>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发私信" width="420px">
      <el-form label-width="72px">
        <el-form-item label="收信人ID">
          <el-input v-model="form.toId" placeholder="输入对方用户ID" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="send">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getConversations, sendMessage } from '@/api/message'
import { getNoticeSummary } from '@/api/notice'
import { getUserProfile } from '@/api/user'

const router = useRouter()
const activeTab = ref('private')
const conversations = ref([])
const unreadTotal = ref(0)
const dialogVisible = ref(false)
const form = ref({ toId: '', content: '' })
const userCache = ref({})
const noticeCards = ref([])

const openDialog = () => (dialogVisible.value = true)

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function load() {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  const res = await getConversations({ userId, page: 1, pageSize: 20 })
  const data = res?.data?.data || res?.data || []
  conversations.value = Array.isArray(data) ? data : []
  unreadTotal.value = conversations.value.reduce((s, it) => s + (it.unread || 0), 0)
  // 预取对方用户信息
  await Promise.all(conversations.value.map((it) => fetchPeer(it)))
}

async function send() {
  const fromId = Number(localStorage.getItem('userId'))
  const toId = Number(form.value.toId)
  const content = (form.value.content || '').trim()
  if (!fromId) return ElMessage.warning('请先登录')
  if (!toId) return ElMessage.warning('请输入收信人ID')
  if (!content) return ElMessage.warning('请输入内容')
  try {
    await sendMessage({ fromId, toId, content })
    ElMessage.success('发送成功')
    dialogVisible.value = false
    form.value = { toId: '', content: '' }
    load()
  } catch (e) {
    // 后端会用统一的 Result.fail 返回错误，此处已由拦截器弹出提示
  }
}

function openConversation(item) {
  if (!item?.conversationId) return
  router.push(`/messages/${item.conversationId}`)
}

function openProfile(item) {
  const pid = parsePeerId(item.conversationId)
  if (!pid) return
  router.push(`/user/${pid}`)
}

function parsePeerId(cid) {
  if (!cid) return null
  const me = Number(localStorage.getItem('userId'))
  const parts = String(cid).split('_').map(v => Number(v))
  if (parts.length !== 2 || !me) return null
  return parts[0] === me ? parts[1] : parts[0]
}

async function fetchPeer(item) {
  const pid = parsePeerId(item.conversationId)
  if (!pid) return null
  if (userCache.value[pid]) return userCache.value[pid]
  try {
    const res = await getUserProfile(pid)
    const data = res?.data
    const info = {
      id: pid,
      username: data?.user?.username || data?.username || '',
      headerUrl: data?.user?.headerUrl || data?.headerUrl || ''
    }
    userCache.value[pid] = info
    return info
  } catch (e) {
    const info = { id: pid, username: '', headerUrl: '' }
    userCache.value[pid] = info
    return info
  }
}

function peerInfo(item) {
  const pid = parsePeerId(item.conversationId)
  return (pid && userCache.value[pid]) || { id: pid, username: '', headerUrl: '' }
}

function avatarInitial(name) {
  if (!name) return '友'
  return name.slice(0, 1)
}

onMounted(load)

const switchNotify = async () => {
  activeTab.value = 'notify'
  const res = await getNoticeSummary()
  const data = res?.data || {}
  const topics = ['comment','like','follow']
  const titleMap = { comment: '评论', like: '赞', follow: '关注' }
  noticeCards.value = await Promise.all(topics.map(async (t) => {
    const card = data[t] || {}
    const latest = card.latest || {}
    let username = ''
    if (latest.userId) {
      try {
        const up = await getUserProfile(latest.userId)
        username = up?.data?.username || up?.data?.user?.username || ''
      } catch {}
    }
    return {
      key: t,
      title: titleMap[t],
      snippet: buildSnippet(t, username),
      total: card.total || 0,
      unread: card.unread || 0,
      time: formatTime(card.time)
    }
  }))
}

function buildSnippet(topic, username){
  const name = username || 'nowcoder'
  if (topic==='comment') return `用户 ${name} 评论了你的帖子 ...`
  if (topic==='like') return `用户 ${name} 点赞了你的帖子 ...`
  if (topic==='follow') return `用户 ${name} 关注了你 ...`
  return ''
}

function openNoticeList(topic){
  // 预留：可跳转到 notices 详情页面；目前仅提示
}
</script>

<style scoped>
.messages-page { padding: 0; }
.msg-card { padding: 0 0 10px; width: 56.25%; margin: 0 auto; }
.msg-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 15px; border-bottom: 1px solid #ebeef5;
}
.tabs { display: flex; gap: 20px; }
.tabs a { color: #606266; cursor: pointer; text-decoration: none; position: relative; }
.tabs a.active { color: #409eff; }
.list { max-height: 70vh; }
.conv-item { display: flex; align-items: center; padding: 14px 16px; border-bottom: 1px solid #f2f2f2; }
.conv-item:hover { background: #fafafa; }
.conv-content { flex: 1; margin-left: 12px; }
.conv-title { font-weight: 600; margin-bottom: 6px; }
.conv-snippet { color: #909399; font-size: 13px; cursor: pointer; }
.conv-avatar { cursor: pointer; }
.conv-meta { text-align: right; width: 140px; color: #909399; }
.time { font-size: 12px; margin-bottom: 4px; }

/* 系统通知卡片样式（参考原型） */
.notice-section { padding: 0 0 16px; }
.notice-card {
  display: flex; align-items: center;
  padding: 14px 16px; border-bottom: 1px solid #f2f2f2;
}
.notice-card .left { width: 120px; display: flex; align-items: center; gap: 10px; }
.notice-card .icon { width: 44px; height: 44px; border-radius: 50%; background: #f5f7fa; display:flex; align-items:center; justify-content:center; }
.notice-card .icon.comment { background: #e8f4ff; }
.notice-card .icon.like { background: #fff3e8; }
.notice-card .icon.follow { background: #ffeef2; }
.notice-card .title { font-size: 16px; color: #606266; }
.notice-card .content { flex: 1; color: #303133; }
.notice-card .snippet { color: #606266; }
.notice-card .meta { width: 180px; text-align: right; color: #909399; }
.notice-card .link { color: #409eff; cursor: pointer; }
</style>


