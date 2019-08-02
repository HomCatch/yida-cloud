import { getRand } from '@/util/index'
export function downloadFile(data, name) {
    const blob = new Blob([data], {
        type:
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    });
    const randomNum = getRand(100000, 999999);
    var downloadElement = document.createElement("a");
    var href = window.URL.createObjectURL(blob); //创建下载的链接
    downloadElement.href = href;
    downloadElement.download = `${name + randomNum}.xlsx`; //下载后文件名
    document.body.appendChild(downloadElement);
    downloadElement.click(); //点击下载
    document.body.removeChild(downloadElement); //下载完成移除元素
    window.URL.revokeObjectURL(href); //释放掉blob对象
}