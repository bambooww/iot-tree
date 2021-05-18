<template>
  <div class="input-group">
    <input type="text" class="form-control" placeholder="add shopping list item" v-model="newItem">
    <span class="input-group-btn">
      <button class="btn btn-primary" @click="emitAdd">
        <i class="fa fa-plus-square-o fa-lg"> </i><span>Add</span>
      </button>
    </span>
  </div>
</template>
 
<script>
export default {
  data() {
    return {
      newItem: ''
    }
  },
  methods: {
    emitAdd() {
      this.$emit('addItem', this.newItem);
    }
  }
}
</script>
 
<style>
</style>