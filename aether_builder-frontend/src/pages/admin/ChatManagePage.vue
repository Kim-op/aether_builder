<template>
  <div id="chatManagePage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">对话管理</h2>
      <p class="page-desc">管理系统中所有用户与AI的对话记录</p>
    </div>

    <!-- 搜索表单 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search-form">
        <a-form-item label="消息内容">
          <a-input v-model:value="searchParams.message" placeholder="输入消息内容" class="search-input" />
        </a-form-item>
        <a-form-item label="消息类型">
          <a-select v-model:value="searchParams.messageType" placeholder="选择消息类型" class="search-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="user">用户消息</a-select-option>
            <a-select-option value="assistant">AI消息</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="应用ID">
          <a-input v-model:value="searchParams.appId" placeholder="输入应用ID" class="search-input" />
        </a-form-item>
        <a-form-item label="用户ID">
          <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" class="search-input" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" class="search-btn">
            <SearchOutlined class="search-icon" />
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 表格 -->
    <a-card class="table-card">
      <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange"
        :scroll="{ x: 1400 }" class="chat-table" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'message'">
            <a-tooltip :title="record.message" placement="topLeft">
              <div class="message-text">{{ record.message }}</div>
            </a-tooltip>
          </template>
          <template v-else-if="column.dataIndex === 'messageType'">
            <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'" class="message-type-tag">
              <template v-if="record.messageType === 'user'">
                <UserOutlined class="tag-icon" />
                用户消息
              </template>
              <template v-else>
                <RobotOutlined class="tag-icon" />
                AI消息
              </template>
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            <span class="create-time">{{ formatTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space size="middle" class="action-buttons">
              <a-button type="primary" size="small" class="view-btn" @click="viewAppChat(record.appId)">
                <MessageOutlined class="btn-icon" />
                查看对话
              </a-button>
              <a-popconfirm title="确定要删除这条消息吗？" @confirm="deleteMessage(record.id)" ok-text="确认" cancel-text="取消">
                <a-button danger size="small" class="delete-btn" slot="reference">
                  <DeleteOutlined class="btn-icon" />
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'
import { SearchOutlined, UserOutlined, RobotOutlined, MessageOutlined, DeleteOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
    sorter: (a: any, b: any) => a.id - b.id,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 300,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 120,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 100,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
    sorter: (a: any, b: any) => dayjs(a.createTime).valueOf() - dayjs(b.createTime).valueOf(),
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.ChatHistory[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ChatHistoryQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
    pageSizeOptions: ['10', '20', '50', '100'],
    showQuickJumper: true,
  }
})

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 查看应用对话
const viewAppChat = (appId: number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 删除消息
const deleteMessage = async (id: number | undefined) => {
  if (!id) return

  try {
    // 注意：这里需要后端提供删除对话历史的接口
    // 目前先显示成功，实际实现需要调用删除接口
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#chatManagePage {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 64px);
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 28px;
  position: relative;
  padding-bottom: 16px;
}

.page-header::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 48px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 3px;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title::before {
  content: '';
  width: 4px;
  height: 24px;
  background: #3b82f6;
  border-radius: 2px;
}

.page-desc {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
}

.search-card:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.search-form {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.search-input,
.search-select {
  width: 180px;
  height: 40px;
  border-radius: 8px;
  border-color: rgba(226, 232, 240, 0.8);
  transition: all 0.2s ease;
}

.search-input:focus,
.search-select:focus {
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  transition: all 0.25s ease;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
  display: flex;
  align-items: center;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
}

.search-icon {
  margin-right: 6px;
  font-size: 14px;
}

/* 表格卡片样式 */
.table-card {
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  transition: all 0.3s ease;
}

.table-card:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 表格样式 */
.chat-table {
  border-collapse: separate;
  border-spacing: 0;
}

.ant-table-thead>tr>th {
  background: #f1f5f9;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-weight: 600;
  color: #334155;
  padding: 14px 16px;
  text-align: left;
}

.ant-table-tbody>tr>td {
  padding: 16px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  transition: background 0.2s ease;
  vertical-align: middle;
}

.ant-table-tbody>tr:hover>td {
  background: rgba(59, 130, 246, 0.02);
}

.ant-table-pagination {
  padding: 16px 20px;
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  background: white;
}

/* 消息内容样式 */
.message-text {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 4px 0;
  transition: color 0.2s ease;
  line-height: 1.4;
}

.message-text:hover {
  color: #3b82f6;
  cursor: pointer;
}

/* 消息类型标签样式 */
.message-type-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-icon {
  font-size: 12px;
}

/* 创建时间样式 */
.create-time {
  color: #64748b;
  font-size: 13px;
  white-space: nowrap;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.view-btn,
.delete-btn {
  border-radius: 6px;
  height: 32px;
  padding: 0 12px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.btn-icon {
  font-size: 14px;
}

.view-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
  border-color: #3b82f6;
  color: white;
}

.view-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border-color: #2563eb;
  transform: translateY(-2px);
}

.delete-btn {
  background: rgba(220, 38, 38, 0.1);
  color: #dc2626;
  border-color: rgba(220, 38, 38, 0.2);
}

.delete-btn:hover {
  background: rgba(220, 38, 38, 0.15);
  color: #dc2626;
  border-color: rgba(220, 38, 38, 0.3);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1200px) {

  .search-input,
  .search-select {
    width: 160px;
  }
}

@media (max-width: 992px) {
  .search-form {
    gap: 12px;
  }

  .search-input,
  .search-select {
    width: 140px;
  }
}

@media (max-width: 768px) {
  #chatManagePage {
    padding: 16px;
  }

  .page-title {
    font-size: 20px;
  }

  .search-form {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .search-input,
  .search-select {
    width: 100%;
  }

  .ant-table {
    font-size: 13px;
  }

  .ant-table-thead>tr>th,
  .ant-table-tbody>tr>td {
    padding: 12px 8px;
  }

  .message-text {
    max-width: 200px;
  }

  .action-buttons {
    flex-wrap: wrap;
  }

  .view-btn,
  .delete-btn {
    padding: 0 10px;
    font-size: 12px;
  }

  .btn-icon {
    margin-right: 0;
  }
}

@media (max-width: 480px) {
  .page-desc {
    display: none;
  }

  .search-btn {
    width: 100%;
    justify-content: center;
  }

  .message-text {
    max-width: 150px;
  }
}
</style>