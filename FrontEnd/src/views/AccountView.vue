<template>
  <div>
    <div>
      <div class="card">
        <div v-if="data.accountDetails" class="account-details">
          <h2>Account Details <el-button style="float:right" type="plain" @click="goBackHome">Back to homepage</el-button></h2>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Account ID">{{ data.accountDetails.id }}</el-descriptions-item>
            <el-descriptions-item label="Account Number">{{ data.accountDetails.accountNumber }}</el-descriptions-item>
            <el-descriptions-item label="Balance">{{ data.accountDetails.balance }}</el-descriptions-item>
            <el-descriptions-item label="Currency">{{ data.accountDetails.currency }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div v-else>
          <el-empty description="Loading account details..." />
        </div>
      </div>
      <div class="card">
        <el-input
          style="width: 300px; margin-right: 10px"
          v-model="data.transactionNumber"
          placeholder="Search by Transaction number"
        ></el-input>
        <el-button type="primary" @click="searchByTransactionNumber">Search</el-button>
        <el-button type="danger" @click="data.transactionNumber = ''">Reset</el-button>
      </div>
      <div class="card">
        <el-button type="success" @click="dialogFormVisible = true">Create New Transaction</el-button>
      </div>
      <div class="card">
        <el-table
          :data="data.tableData"
          stripe
          :default-sort="{ prop: 'date', order: 'descending' }"
          :sort-orders="['ascending', 'descending', null]"
          @sort-change="handleSortChange"
        >
          <el-table-column prop="transactionNumber" label="Transaction Number" sortable="custom"></el-table-column>
          <el-table-column prop="type" label="Type" sortable="custom"></el-table-column>
          <el-table-column prop="amount" label="Amount" sortable="custom"></el-table-column>
          <el-table-column prop="balanceBefore" label="Balance Before" sortable="custom"></el-table-column>
          <el-table-column prop="balanceAfter" label="Balance After" sortable="custom"></el-table-column>
          <el-table-column prop="description" label="Description" sortable="custom"></el-table-column>
          <el-table-column prop="referenceNumber" label="Reference Number" sortable="custom"></el-table-column>
          <el-table-column prop="relatedAccountId" label="Related Account Id" sortable="custom"></el-table-column>
          <el-table-column prop="transactionTime" label="Transaction Time" sortable="custom"></el-table-column>
          <el-table-column label="Action" width="160">
            <!-- 定义插槽, scope允许父组件向插槽传递数据。-->
            <template #default="scope">
              <el-button type="primary" :icon="Edit" @click="handleEdit(scope.row)"></el-button>
              <el-button type="danger" :icon="Delete" @click="handleDelete(scope.row)"></el-button>
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


  <el-dialog v-model="dialogFormVisible" title="Create Transaction" width="600px">
    <el-form ref="transactionFormRef" @submit.prevent="createTransaction" :model="transactionForm">
      <el-form-item label="Transaction Type" :label-width="formLabelWidth">
        <el-select v-model="transactionForm.type" placeholder="Select transaction type">
          <el-option label="Deposit" value="DEPOSIT" />
          <el-option label="Withdraw" value="WITHDRAW" />
          <el-option label="Transfer" value="TRANSFER" />
        </el-select>
      </el-form-item>

      <el-form-item
        v-if="transactionForm.type === 'TRANSFER'"
        label="To Account ID"
        :label-width="formLabelWidth"
      >
        <el-input v-model="transactionForm.toAccountId" type="number" />
      </el-form-item>
      <el-form-item label="Amount" :label-width="formLabelWidth">
        <el-input v-model="transactionForm.amount" type="number" />
      </el-form-item>
      <el-form-item label="Description" :label-width="formLabelWidth">
        <el-input
          v-model="transactionForm.description"
          type="textarea"
          :rows="3"
          :autosize="{ minRows: 2, maxRows: 6 }"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="createTransaction">Confirm</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="editDialogVisible" title="Edit Transaction" width="500">
    <el-form>
      <el-form-item label="Description" :label-width="formLabelWidth">
        <el-input
          v-model="editForm.description"
          type="textarea"
          :rows="3"
          :autosize="{ minRows: 2, maxRows: 6 }"
        />
      </el-form-item>
      <el-form-item label="Reference Number" :label-width="formLabelWidth">
        <el-input v-model="editForm.referenceNumber" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="updateTransaction">Save</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const apiUrl = import.meta.env.VITE_API_URL
const router = useRouter()

const props = defineProps({
  id: {
    type: String,
    required: true
  }
})

const dialogFormVisible = ref(false)
const editDialogVisible = ref(false)
const formLabelWidth = '140px'

const transactionFormRef = ref(null)

const transactionForm = reactive({
  type: '',
  toAccountId: null,
  amount: '',
  description: ''
})

const editForm = reactive({
  id: null,
  description: '',
  referenceNumber: ''
})

const goBackHome = () => {
  router.push({ name: 'manage-home' })
}


const data = reactive({
  background: true,
  transactionNumber: '',
  tableData: [],
  pageNum: 0,
  pageSize: 5,
  total: 46,
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

const searchByTransactionNumber = () => {
  data.params = `transactionNumber=${data.transactionNumber}`
  fetchData()
}

const fetchData = async () => {
  console.log('fetching data')
  try {
    var page_num = data.pageNum > 0 ? data.pageNum - 1 : 0;
    const response = await fetch(
      `${apiUrl}/api/transactions?accountId=${props.id}&page=${page_num}&size=${data.pageSize}&${data.params}`,
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

const fetchAccountDetails = async () => {
  console.log('fetching account details for ID:', props.id)
  try {
    const response = await fetch(
      `${apiUrl}/api/accounts/${props.id}`,
      {
        method: 'GET',
      },
    )

    const result = await response.json()
    if (result.code === 0) {
      data.accountDetails = result.data
      console.dir(result.data)
    }
  } catch (error) {
    console.error('Fetch account details failed:', error)
  }
}

const handleEdit = (row) => {
  editForm.id = row.id
  editForm.description = row.description
  editForm.referenceNumber = row.referenceNumber
  editDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    const response = await fetch(
      `${apiUrl}/api/transactions/${row.id}`,
      {
        method: 'DELETE',
      },
    )
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('Transaction deleted successfully')
      fetchData() // Refresh the table
    } else {
      ElMessage.error(result.message || 'Failed to delete transaction')
    }
  } catch (error) {
    console.error('Delete transaction failed:', error)
    ElMessage.error('Delete transaction failed')
  }
}

const updateTransaction = async () => {
  try {
    const response = await fetch(
      `${apiUrl}/api/transactions/${editForm.id}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          description: editForm.description,
          referenceNumber: editForm.referenceNumber
        })
      }
    )

    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('Transaction updated successfully')
      editDialogVisible.value = false
      fetchData() // Refresh the table
    } else {
      ElMessage.error(result.message || 'Failed to update transaction')
    }
  } catch (error) {
    console.error('Update transaction failed:', error)
    ElMessage.error('Failed to update transaction')
  }
}

const createTransaction = async () => {
  try {
    if (isNaN(transactionForm.amount) || transactionForm.amount <= 0) {
      ElMessage.error('Please enter a valid amount')
      return
    }

    const requestData = {
      type: transactionForm.type,
      fromAccountId: props.id,
      toAccountId: transactionForm.toAccountId,
      amount: Number(transactionForm.amount),
      description: transactionForm.description
    }

    const response = await fetch(
      `${apiUrl}/api/transactions`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData)
      }
    )

    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('Transaction created successfully')
      dialogFormVisible.value = false
      fetchAccountDetails() // Refresh the detail
      fetchData() // Refresh the table
    } else {
      ElMessage.error(result.message || 'Failed to create transaction')
    }
  } catch (error) {
    console.error('Create transaction failed:', error)
    ElMessage.error('Failed to create transaction')
  }
}

onMounted(() => {
  console.log('mounted')
  fetchAccountDetails()
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
