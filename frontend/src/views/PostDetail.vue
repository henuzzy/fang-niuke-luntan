<template>
  <div class="post-detail">
    <!-- 帖子内容卡片 -->
    <el-card v-loading="loading" class="post-card">
      <div class="post-header">
        <div class="left">
          <el-avatar :size="48" :src="author.headerUrl || getDefaultAvatar()" />
          <div class="meta">
            <div class="title">{{ post.title }}</div>
            <div class="sub">{{ author.username }} · {{ formatTime(post.createTime) }}</div>
          </div>
        </div>
        <div class="right">
          <el-button size="small" :type="likeStatus ? 'danger' : 'default'" @click="handleLike">
            {{ likeStatus ? '已赞' : '赞' }} {{ likeCount }}
          </el-button>
          <el-button size="small" type="info" v-if="!isCurrentUser" @click="handleFollow">
            {{ hasFollowed ? '已关注' : '关注TA' }}
          </el-button>
        </div>
      </div>
      <div class="post-content" v-html="post.content" />
    </el-card>

    <!-- 评论卡片 -->
    <el-card class="comments-card">
      <div class="comments">
        <h3 class="comments-title">评论</h3>
        <div v-for="c in comments" :key="c.id" class="comment">
          <div class="c-header">
            <span class="name">{{ c.username }}</span>
            <span class="time">{{ formatTime(c.createTime) }}</span>
            <span class="actions">
              <span class="like" @click="handleLikeComment(c)">赞 {{ c.likeCount }}</span>
              <span class="reply-link" @click="c.showReply = !c.showReply">回复</span>
            </span>
          </div>
          <div class="c-body">{{ c.content }}</div>
          <div class="replies" v-if="c.replies && c.replies.length">
            <div v-for="r in c.replies" :key="r.id" class="reply">
              <span class="name">{{ r.username }}</span>：{{ r.content }}
              <span class="time">{{ formatTime(r.createTime) }}</span>
              <span class="like" @click="handleLikeReply(r)">赞 {{ r.likeCount }}</span>
            </div>
          </div>
          <div class="reply-box" v-if="c.showReply">
            <el-input v-model="c.replyText" placeholder="请输入你的观点" />
            <el-button type="primary" size="small" class="reply-btn" @click="submitReply(c)">回 复</el-button>
          </div>
        </div>
      </div>
    </el-card>
    <!-- 评论分页导航 -->
    <!-- 评论分页导航 -->
    <el-card class="comments-card" v-if="commentPage.totalPages && commentPage.totalPages > 1">
      <div class="pager">
        <span class="btn" @click="goFirst">首页</span>
        <span class="btn" @click="goPrev">上一页</span>
        <span
          v-for="p in visiblePages"
          :key="p"
          class="num"
          :class="{ active: p === commentPage.page }"
          @click="goTo(p)"
        >{{ p }}</span>
        <span class="btn" @click="goNext">下一页</span>
        <span class="btn" @click="goLast">末页</span>
      </div>
    </el-card>

    <!-- 发表评论卡片（回帖） -->
    <el-card class="reply-card">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="8"
        placeholder="在这里畅所欲言你的看法吧!"
      />
      <div class="reply-actions">
        <el-button type="primary" :loading="publishing" @click="publishComment">回 帖</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getPostDetail, likePost, likeComment, replyComment, commentPost } from '@/api/post'
import { followUser, unfollowUser, getUserProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const post = ref({})
const author = ref({})
const comments = ref([])
const commentPage = ref({ page: 1, pageSize: 10, total: 0, totalPages: 0 })
const newComment = ref('')
const publishing = ref(false)
const likeCount = ref(0)
const likeStatus = ref(0)
const hasFollowed = ref(false)

const isCurrentUser = computed(() => {
  const uid = localStorage.getItem('userId')
  return uid && author.value && String(uid) === String(author.value.id)
})

const fetchDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getPostDetail(id, { page: commentPage.value.page, pageSize: commentPage.value.pageSize })
    post.value = res.data.post || {}
    author.value = res.data.author || {}
    comments.value = res.data.comments || []
    likeCount.value = res.data.likeCount || 0
    likeStatus.value = res.data.likeStatus || 0
    if (res.data.commentPage) {
      commentPage.value = res.data.commentPage
    }
    // 个人主页接口中返回 hasFollowed，更权威；若无则根据当前页估算
    try {
      if (author.value.id) {
        const prof = await getUserProfile(author.value.id)
        hasFollowed.value = !!prof.data.hasFollowed
      }
    } catch {}
  } catch (e) {
    ElMessage.error('获取帖子详情失败')
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  try {
    const id = route.params.id
    const res = await likePost(id)
    likeCount.value = res.data.likeCount
    likeStatus.value = res.data.likeStatus
  } catch (e) {
    ElMessage.error('操作失败，请先登录')
  }
}

const handleFollow = async () => {
  try {
    if (!author.value.id) return
    if (hasFollowed.value) {
      await unfollowUser(author.value.id)
      hasFollowed.value = false
    } else {
      await followUser(author.value.id)
      hasFollowed.value = true
    }
  } catch (e) {
    ElMessage.error('操作失败，请先登录')
  }
}

const handleLikeComment = async (c) => {
  try {
    const res = await likeComment(c.id)
    c.likeCount = res.data.likeCount
  } catch {}
}
const handleLikeReply = async (r) => {
  try {
    const res = await likeComment(r.id)
    r.likeCount = res.data.likeCount
  } catch {}
}
const submitReply = async (c) => {
  if (!c.replyText) return
  try {
    const id = route.params.id
    await replyComment(id, c.id, { content: c.replyText })
    ElMessage.success('回复成功')
    c.replyText = ''
    c.showReply = false
    fetchDetail()
  } catch {}
}

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString()
}

const getDefaultAvatar = () => {
  return localStorage.getItem('userAvatar') || ''
}

onMounted(fetchDetail)

// 分页逻辑
const visiblePages = computed(() => {
  const total = Number(commentPage.value.totalPages || 0)
  const current = Number(commentPage.value.page || 1)
  const windowSize = 5
  if (total <= windowSize) return Array.from({ length: total }, (_, i) => i + 1)
  let start = Math.max(1, current - Math.floor(windowSize / 2))
  let end = Math.min(total, start + windowSize - 1)
  start = Math.max(1, end - windowSize + 1)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

const goTo = async (p) => {
  if (p === commentPage.value.page) return
  commentPage.value.page = p
  await fetchDetail()
}
const goFirst = () => goTo(1)
const goLast = () => goTo(commentPage.value.totalPages || 1)
const goPrev = () => goTo(Math.max(1, (commentPage.value.page || 1) - 1))
const goNext = () => goTo(Math.min(commentPage.value.totalPages || 1, (commentPage.value.page || 1) + 1))

// 发表新评论
const publishComment = async () => {
  const content = (newComment.value || '').trim()
  if (!content) return
  publishing.value = true
  try {
    const id = route.params.id
    await commentPost(id, { content })
    newComment.value = ''
    // 发表后回到第1页刷新
    commentPage.value.page = 1
    await fetchDetail()
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
.post-detail {
  max-width: 1000px;
  margin: 0 auto;
}
.post-card { padding: 20px; }
.post-header { display: flex; align-items: center; justify-content: space-between; }
.post-header .left { display: flex; align-items: center; }
.post-header .meta { margin-left: 12px; }
.post-header .title { font-size: 18px; font-weight: 600; }
.post-header .sub { color: #888; font-size: 12px; }
.post-content { margin-top: 16px; line-height: 1.8; }
.comments { margin-top: 24px; }
.comment { padding: 12px 0; border-bottom: 1px solid #eee; }
.c-header { color: #666; font-size: 12px; display:flex; gap:10px; align-items:center; }
.c-header .name { color: #333; }
.c-header .actions { margin-left: auto; display:flex; gap:12px; }
.c-body { margin: 6px 0 8px; }
.reply { color: #666; font-size: 12px; margin: 4px 0; }
.like { cursor: pointer; color: #409eff; }
.reply-link { cursor:pointer; color:#409eff; }
.reply-box { margin-top: 8px; display:flex; gap:8px; align-items:center; }
.reply-btn { margin-left: 8px; }

/* 分页导航（模仿原型：首页 上一页 1 2 3 4 5 下一页 末页） */
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  gap: 8px;
}
.pager .btn, .pager .num {
  padding: 6px 12px;
  border: 1px solid #e6e6e6;
  background: #fff;
  color: #409eff;
  cursor: pointer;
}
.pager .num.active {
  background: #409eff;
  color: #fff;
}
.pager .btn:hover, .pager .num:hover { filter: brightness(0.95); }

/* 回帖卡片 */
.reply-card { margin-top: 16px; padding: 16px; }
.reply-actions { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>

