<template>
  <div id="appManagePage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">应用管理</h2>
      <p class="page-desc">管理系统中所有生成的应用资源</p>
    </div>

    <!-- 搜索表单 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search-form">
        <a-form-item label="应用名称">
          <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" class="search-input" />
        </a-form-item>
        <a-form-item label="创建者">
          <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" class="search-input" />
        </a-form-item>
        <a-form-item label="生成类型">
          <a-select v-model:value="searchParams.codeGenType" placeholder="选择生成类型" class="search-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="option in CODE_GEN_TYPE_OPTIONS" :key="option.value" :value="option.value">
              {{ option.label }}
            </a-select-option>
          </a-select>
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
        :scroll="{ x: 1200 }" class="app-table" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'cover'">
            <div class="cover-container">
              <a-image v-if="record.cover" :src="record.cover" :width="80" :height="60" class="app-cover" />
              <div v-else class="no-cover">无封面</div>
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'initPrompt'">
            <a-tooltip :title="record.initPrompt">
              <div class="prompt-text">{{ record.initPrompt }}</div>
            </a-tooltip>
          </template>
          <template v-else-if="column.dataIndex === 'codeGenType'">
            <a-tag class="type-tag" :color="getTypeColor(record.codeGenType)">
              {{ formatCodeGenType(record.codeGenType) }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'priority'">
            <a-tag v-if="record.priority === 99" color="gold" class="priority-tag">精选</a-tag>
            <span v-else class="priority-value">{{ record.priority || 0 }}</span>
          </template>
          <template v-else-if="column.dataIndex === 'deployedTime'">
            <span v-if="record.deployedTime" class="deployed-time">
              {{ formatTime(record.deployedTime) }}
            </span>
            <span v-else class="text-gray">未部署</span>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.dataIndex === 'user'">
            <UserInfo :user="record.user" size="small" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space size="middle" class="action-buttons">
              <a-button type="primary" size="small" class="edit-btn" @click="editApp(record)">
                <EditOutlined class="btn-icon" />
                编辑
              </a-button>
              <a-button type="default" size="small" @click="toggleFeatured(record)"
                :class="{ 'featured-btn': record.priority === 99 }">
                <StarOutlined class="btn-icon" />
                {{ record.priority === 99 ? '取消精选' : '精选' }}
              </a-button>
              <a-popconfirm title="确定要删除这个应用吗？" @confirm="deleteApp(record.id)">
                <a-button danger size="small" class="delete-btn">
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
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'
import { SearchOutlined, EditOutlined, StarOutlined, DeleteOutlined } from '@ant-design/icons-vue'

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
    title: '应用名称',
    dataIndex: 'appName',
    width: 150,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 100,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 200,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 120,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 100,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 160,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 140,
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
    width: 240,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
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

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
      // 刷新数据
      fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}

// 根据生成类型获取标签颜色
const getTypeColor = (type: string) => {
  const typeMap: Record<string, string> = {
    'vue': 'blue',
    'react': 'purple',
    'html': 'orange',
    'angular': 'red',
    'flutter': 'green'
  }
  return typeMap[type] || 'default'
}
</script>

<style scoped>
#appManagePage {
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
  width: 200px;
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
.app-table {
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

/* 应用封面样式 */
.cover-container {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease;
}

.cover-container:hover {
  transform: scale(1.05);
}

.app-cover {
  object-fit: cover;
  border-radius: 6px;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 12px;
  border-radius: 6px;
}

/* 提示词样式 */
.prompt-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
}

.prompt-text:hover {
  color: #3b82f6;
}

/* 类型标签样式 */
.type-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* 优先级样式 */
.priority-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(250, 173, 20, 0.1);
  color: #d48806;
  border-color: rgba(250, 173, 20, 0.2);
}

.priority-value {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  background: rgba(226, 232, 240, 0.5);
  color: #64748b;
}

/* 部署时间样式 */
.deployed-time {
  color: #334155;
  font-size: 13px;
}

.text-gray {
  color: #94a3b8;
  font-size: 13px;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn,
.featured-btn,
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

.edit-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
  border-color: #3b82f6;
  color: white;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border-color: #2563eb;
  transform: translateY(-2px);
}

.featured-btn {
  background: #faad14;
  border-color: #faad14;
  color: white;
}

.featured-btn:hover {
  background: #d48806;
  border-color: #d48806;
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
    width: 180px;
  }
}

@media (max-width: 992px) {

  .search-input,
  .search-select {
    width: 160px;
  }
}

@media (max-width: 768px) {
  #appManagePage {
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

  .action-buttons {
    flex-wrap: wrap;
  }

  .edit-btn,
  .featured-btn,
  .delete-btn {
    padding: 0 8px;
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
}
</style>