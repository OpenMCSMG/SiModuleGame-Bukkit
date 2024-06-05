import requests
from PIL import Image
from pypinyin import pinyin, lazy_pinyin, Style
import json
import re  # 添加这一行

url = "https://live.cyanbukkit.cn/info/gift/douyin"
path = "E:/MineCraft_Client/Badlion/resourcepacks/6DouYiGift_ResourcePack"

response = requests.get(url)
data = response.json()

da = data['data']
newArr = []

for i in da:
    id = i['id']
    name = i['name']
    url = i['icon_urls'][0]
    # file_name 是个数字，所以要转换成字符串，然后拼接成文件名 并且使用_间隔拼音
    name = re.sub(r'\W+', '_', name)
    file_name = 'coal_' + '_'.join(lazy_pinyin(name)).lower()
    # 根据url下载文件 下载到 E:\MineCraft_Client\Badlion\resourcepacks\礼物图标资源包\assets\minecraft\textures\item 并且 resized 为 256x256
    response = requests.get(url)
    with open(f'{path}/assets/minecraft/textures/item/{file_name}.png', 'wb') as f:
        f.write(response.content)
    # # 读取文件并且 resize
    img = Image.open(f'{path}/assets/minecraft/textures/item/{file_name}.png')
    img = img.resize((256, 256))
    img.save(f'{path}/assets/minecraft/textures/item/{file_name}.png')
    newArr.append({
        'id': id,
        'name': file_name,
        'url': url
    })
    print(f'下载 {name} 的图标到 {path}/assets/minecraft/textures/item/{file_name}.png 成功！')


# 最后创个json文件写{ overrides: [ { predicate: { custom_model_data: 1 }, model: "item/礼物1" }, { predicate: { custom_model_data: 2 }, model: "item/礼物2" } ]

newJson = {
    "parent": "minecraft:item/generated",
    "textures": {
        "layer0": "minecraft:item/coal"
    },
    'overrides': []
}

for i in range(0, 20001):
    # newArr 的id 包含 i 的话就写入新json文件 
    if any(d['id'] == i for d in newArr):
        file_name = next(item for item in newArr if item["id"] == i)["name"]
        # 生成json文件模型json
        with open(f'{path}/assets/minecraft/models/item/{file_name}.json', 'w') as f:
            json.dump({
                'parent': 'minecraft:item/handheld',
                'textures': {
                    'layer0': f'minecraft:item/{file_name}'
                }
            }, f)
        newJson['overrides'].append({
            'predicate': {
                'custom_model_data': i
            },
            'model': f'item/{file_name}'
        })
        print(f'添加 {file_name} 的json成功！')
        # 没有的填原版物品以让所有data按序列来
    else:
        newJson['overrides'].append({
            'predicate': {
                'custom_model_data':i
            },
            'model': f'item/iron_ingot'
        })

# 保存json.json文件到运行目录
with open(f'{path}/assets/minecraft/models/item/coal.json', 'w') as f:
    json.dump(newJson, f)
    print('保存json.json文件成功！')