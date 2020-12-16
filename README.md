[![](https://jitpack.io/v/xuqiqiang/RecyclerTouchHelper.svg)](https://jitpack.io/#xuqiqiang/RecyclerTouchHelper)

# RecyclerTouchHelper
RecyclerView子元素拖拽功能，实现重排序和删除

- 支持列表和网格的排序+删除
- 支持带header和footer的列表和网格
- 支持非线性排列的列表和网格
- 可高度扩展

<figure class="half">
    <img src="https://iskeen.oss-cn-beijing.aliyuncs.com/chatImg/file_1608092799344Screenshot_1.jpg" width="300px" alt="Screenshot_1" />
    <img src="https://iskeen.oss-cn-beijing.aliyuncs.com/chatImg/file_1608092829805Screenshot_2.jpg" width="300px" alt="Screenshot_2" />
</figure>

## Gradle dependency

```
allprojects {
        repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
	implementation 'com.github.xuqiqiang:RecyclerTouchHelper:1.0.6'
}
```


## Usage

[Simple demo](https://github.com/xuqiqiang/RecyclerTouchHelper/blob/master/app/src/main/java/com/xuqiqiang/view/touch/demo/DemoActivity.java)

## Apache License
       Apache License

       Copyright [2020] [xuqiqiang]

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

