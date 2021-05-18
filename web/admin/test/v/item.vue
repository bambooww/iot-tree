<template>
  <li :class="{'removed': item.checked}" class="list-group-item">
    <div class="checkbox">
      <label>
        <input type="checkbox" v-model="item.checked">
        <span>{{ item.text }}</span>
      </label>
    </div>
  </li>
</template>
 
<script>
export default {
  props: ['item']
}
</script>
 
<style>
.removed {
  color: gray;
}
 
.removed span {
  text-decoration: line-through;
}
</style>