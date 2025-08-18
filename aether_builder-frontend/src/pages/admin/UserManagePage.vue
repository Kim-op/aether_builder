<template>
  <div id="userManagePage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <p class="page-desc">管理系统用户账号及权限设置</p>
    </div>

    <!-- 搜索表单 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search-form">
        <a-form-item label="账号">
          <a-input
            v-model:value="searchParams.userAccount"
            placeholder="输入账号"
            class="search-input"
          />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input
            v-model:value="searchParams.userName"
            placeholder="输入用户名"
            class="search-input"
          />
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
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        @change="doTableChange"
        class="user-table"
        row-key="id"
        :scroll="{ x: 'max-content' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userAvatar'">
            <a-avatar :src="record.userAvatar" class="user-avatar" shape="square" />
          </template>
          <template v-else-if="column.dataIndex === 'userRole'">
            <div v-if="record.userRole === 'admin'">
              <a-tag color="green" class="role-tag admin-tag">管理员</a-tag>
            </div>
            <div v-else>
              <a-tag color="blue" class="role-tag user-tag">普通用户</a-tag>
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-popconfirm
              title="确定要删除该用户吗？"
              @confirm="doDelete(record.id)"
              ok-text="确认"
              cancel-text="取消"
            >
              <a-button danger class="delete-btn" slot="reference">
                <DeleteOutlined class="delete-icon" />
                删除
              </a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { SearchOutlined, DeleteOutlined } from '@ant-design/icons-vue'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    sorter: (a: any, b: any) => a.id - b.id,
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 150,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 150,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 100,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 200,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    sorter: (a: any, b: any) => dayjs(a.createTime).valueOf() - dayjs(b.createTime).valueOf(),
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
  },
]

// 展示的数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

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

// 表格分页变化时的操作
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败: ' + res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
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

.search-input {
  width: 240px;
  height: 40px;
  border-radius: 8px;
  border-color: rgba(226, 232, 240, 0.8);
  transition: all 0.2s ease;
}

.search-input:focus {
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
.user-table {
  border-collapse: separate;
  border-spacing: 0;
}

.ant-table-thead > tr > th {
  background: #f1f5f9;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-weight: 600;
  color: #334155;
  padding: 14px 16px;
  text-align: left;
}

.ant-table-tbody > tr > td {
  padding: 16px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  transition: background 0.2s ease;
}

.ant-table-tbody > tr:hover > td {
  background: rgba(59, 130, 246, 0.02);
}

.ant-table-pagination {
  padding: 16px 20px;
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  background: white;
}

/* 用户头像样式 */
.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: transform 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

/* 角色标签样式 */
.role-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.admin-tag {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
  border-color: rgba(16, 185, 129, 0.2);
}

.user-tag {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.2);
}

/* 删除按钮样式 */
.delete-btn {
  border-radius: 6px;
  height: 36px;
  padding: 0 14px;
  transition: all 0.2s ease;
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

.delete-icon {
  margin-right: 4px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-input {
    width: 200px;
  }
}

@media (max-width: 768px) {
  #userManagePage {
    padding: 16px;
  }

  .page-title {
    font-size: 20px;
  }

  .search-form {
    padding: 16px;
  }

  .search-input {
    width: 100%;
  }

  .ant-table {
    font-size: 13px;
  }

  .ant-table-thead > tr > th,
  .ant-table-tbody > tr > td {
    padding: 12px 8px;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .page-desc {
    display: none;
  }

  .delete-btn {
    padding: 0 10px;
    font-size: 12px;
  }

  .delete-icon {
    margin-right: 2px;
  }
}
</style>