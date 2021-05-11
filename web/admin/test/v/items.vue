<script>
import item from './item'
 
export default {
  props: ['items'],
  components: {
    item
  }
}
</script>
 
<template>
  <ul class="list-group">
    <item v-for="item in items" :key="item.id" :item="item"></item>
  </ul>
</template>
 
<style>
</style>