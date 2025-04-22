<template>
  <div>
    <div>
      <div class="card">
        <h2>User List</h2>
        <el-input
          style="width: 300px; margin-right: 10px"
          v-model="data.name"
          placeholder="Search by Username"
        ></el-input>
        <el-button type="primary" @click="searchByName">Search</el-button>
        <el-button type="danger" @click="data.name = ''">Reset</el-button>
      </div>
      <div class="card">
        <el-table
          :data="data.tableData"
          stripe
          :default-sort="{ prop: 'date', order: 'descending' }"
          :sort-orders="['ascending', 'descending', null]"
          @sort-change="handleSortChange"
        >
          <el-table-column prop="id" label="ID" sortable="custom"></el-table-column>
          <el-table-column prop="username" label="Username" sortable="custom"></el-table-column>
          <el-table-column prop="fullName" label="Full Name" sortable="custom"></el-table-column>
          <el-table-column prop="email" label="Email" sortable="custom"></el-table-column>
          <el-table-column prop="phoneNumber" label="Phone Number" sortable="custom"></el-table-column>
          <el-table-column label="Action" width="160">
            <!-- 定义插槽, scope允许父组件向插槽传递数据。-->
            <template #default="scope">
              <el-button type="primary" @click="viewAccount(scope.row.id)">View Account</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="padding-top: 15px;">
          <el-pagination
            layout="total, prev, pager, next"
            v-model:current-page="data.pageNum"
            v-model:page-size="data.pageSize"
            :page-sizes="[5, 10, 20]"
            :background="data.background"
            :total="data.total"
            @current-change="handleCurrentChange"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const apiUrl = import.meta.env.VITE_API_URL
const router = useRouter()

const viewAccount = (id) => {
  router.push({ name: 'manage-account', params: { id } })
}

const dialogFormVisible = ref(false)
const formLabelWidth = '140px'


const data = reactive({
  background: true,
  name: '',
  tableData: [],
  pageNum: 0,
  pageSize: 5,
  total: 0,
  params: '',
})

const handleCurrentChange = (val) => {
  console.log('current page changed', val)
  data.pageNum = val
  fetchData()
}

const handleSortChange = (column) => {
  console.log('sort changed', column)
  let columnOrder = column.order
  if (column.order === 'ascending') {
    columnOrder = 'asc'
  } else {
    columnOrder = 'desc'
  }
  data.params = `sortField=${column.prop}&direction=${columnOrder}`
  fetchData()
}

const searchByName = () => {
  console.log('searchByName')
  data.params = `username=${data.name}`
  fetchData()
}

const fetchData = async () => {
  console.log('fetching data')
  try {
    var page_num = data.pageNum > 0 ? data.pageNum - 1 : 0;
    const response = await fetch(
      `${apiUrl}/api/users?page=${page_num}&size=${data.pageSize}&${data.params}`,
      {
        method: 'GET',
      },
    )

    const result = await response.json()
    if (result.code === 0) {
      data.tableData = result.data.content
      data.total = result.data.totalElements
      console.dir(result.data)
    }
  } catch (error) {
    console.error('Fetch data failed:', error)
  }
}

onMounted(() => {
  console.log('mounted')
  fetchData()
})
</script>

<style scoped>
.card {
  background-color: white;
  padding: 10px;
  margin: 10px;
  border-radius: 5px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.12);
}
</style>
