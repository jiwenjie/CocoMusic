package com.jiwenjie.cocomusic.utils

import android.content.Context
import android.os.Environment
import java.io.*
import java.util.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/02
 *  desc:
 *  version:1.0
 */
object FileUtils {
    /**
     * 获取APP根目录
     *
     * @return
     */
    private val appDir: String
        get() = Environment.getExternalStorageDirectory().toString() + "/musicCoco/"

    val musicDir: String
        get() {
            val dir = appDir + "Music/"
            return mkdirs(dir)
        }


    val musicCacheDir: String
        get() {
            val dir = appDir + "MusicCache/"
            return mkdirs(dir)
        }

    val imageDir: String
        get() {
            val dir = appDir + "cache/"
            return mkdirs(dir)
        }

    val lrcDir: String
        get() {
            val dir = appDir + "Lyric/"
            return mkdirs(dir)
        }

    val logDir: String
        get() {
            val dir = appDir + "Log/"
            return mkdirs(dir)
        }

    val relativeMusicDir: String
        get() {
            val dir = "hkMusic/Music/"
            return mkdirs(dir)
        }


    /**
     * 判断外部存储是否可用
     *
     * @return true: 可用
     */
    val isSDcardAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    fun getSplashDir(context: Context): String {
        val dir = context.filesDir.toString() + "/splash/"
        return mkdirs(dir)
    }

    private fun mkdirs(dir: String): String {
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    /**
     * 可创建多个文件夹
     * dirPath 文件路径
     */
    fun mkDir(dirPath: String): Boolean {
        val dirArray = dirPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var pathTemp = ""
        var mkdir = false
        for (i in dirArray.indices) {
            pathTemp = pathTemp + "/" + dirArray[i]
            val newF = File(dirArray[0] + pathTemp)
            if (!newF.exists()) {
                mkdir = newF.mkdir()
            }
        }
        return mkdir
    }


    /**
     * 创建文件
     *
     *
     * dirpath 文件目录
     * fileName 文件名称
     */
    fun creatFile(dirPath: String, fileName: String): Boolean {
        val file = File(dirPath, fileName)
        var newFile = false
        if (!file.exists()) {
            try {
                newFile = file.createNewFile()
            } catch (e: IOException) {
                newFile = false
            }

        }
        return newFile
    }

    /**
     * 创建文件
     * filePath 文件路径
     */
    fun creatFile(filePath: String): Boolean {
        val file = File(filePath)
        var newFile = false
        if (!file.exists()) {
            newFile = try {
                file.createNewFile()
            } catch (e: IOException) {
                false
            }

        }
        return newFile
    }

    /**
     * 创建文件
     * file 文件
     */
    fun creatFile(file: File): Boolean {
        var newFile = false
        if (!file.exists()) {
            newFile = try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }

        }
        return newFile
    }

    /**
     * 删除文件
     * dirpath 文件目录
     * fileName 文件名称
     */
    fun delFile(dirpath: String, fileName: String): Boolean {
        val file = File(dirpath, fileName)
        val delete: Boolean
        delete = if (!file.exists() || file.isDirectory) {
            false
        } else {
            file.delete()
        }
        return delete
    }

    /**
     * 删除文件
     * filepath 文件路径
     */
    fun delFile(filepath: String): Boolean {
        val file = File(filepath)
        var delete: Boolean
        delete = if (!file.exists() || file.isDirectory) {
            false
        } else {
            file.delete()
        }
        return delete
    }

    /**
     * 删除文件
     * filepath 文件路径
     */
    fun delFile(filepath: File?): Boolean {
        val delete: Boolean = if (filepath == null || !filepath.exists() || filepath.isDirectory) {
            false
        } else {
            filepath.delete()
        }
        return delete
    }

    /**
     * 删除文件夹
     * dirPath 文件路径
     */
    fun delDir(dirpath: String): Boolean {
        val dir = File(dirpath)
        return deleteDirWihtFile(dir)
    }

    fun deleteDirWihtFile(dir: File?): Boolean {
        var delete = false
        if (dir == null || !dir.exists() || !dir.isDirectory) {
            delete = false
        } else {
            val files = dir.listFiles()
            for (i in files.indices) {
                if (files[i].isFile) {
                    files[i].delete()
                } else if (files[i].isDirectory) {
                    deleteDirWihtFile(files[i])
                }
                delete = dir.delete()// 删除目录本身
            }
        }
        return delete
    }


    /**
     * 修改SD卡上的文件或目录名
     * oldFilePath 旧文件或文件夹路径
     * newFilePath 新文件或文件夹路径
     */
    fun renameFile(oldFilePath: String, newFilePath: String): Boolean {
        val oldFile = File(oldFilePath)
        val newFile = File(newFilePath)
        return oldFile.renameTo(newFile)
    }

//    fun copyFileTo(srcFile: String, destFile: String): Boolean {
//        return copyFileTo(File(srcFile), File(destFile))
//    }

    /**
     * 拷贝一个文件
     * srcFile源文件
     * destFile目标文件
     */
//    fun copyFileTo(srcFile: File, destFile: File): Boolean {
//        var copyFile = false
//        if (!srcFile.exists() || srcFile.isDirectory || destFile.isDirectory) {
//            copyFile = false
//        } else {
//            var `is`: FileInputStream? = null
//            var os: FileOutputStream? = null
//            try {
//                `is` = FileInputStream(srcFile)
//                os = FileOutputStream(destFile)
//                val buffer = ByteArray(1024)
//                var length: Int
//                while ((length = `is`.read(buffer)) > 0) {
//                    os.write(buffer, 0, length)
//                }
//                copyFile = true
//            } catch (e: Exception) {
//                copyFile = false
//            } finally {
//                if (`is` != null) {
//                    try {
//                        `is`.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//                if (os != null) {
//                    try {
//                        os.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//            }
//
//        }
//        return copyFile
//    }

    /**
     * 拷贝目录下的所有文件到指定目录
     * srcDir 原目录
     * destDir 目标目录
     */
//    fun copyFilesTo(srcDir: File, destDir: File): Boolean {
//        if (!srcDir.exists() || !srcDir.isDirectory || !destDir.isDirectory) {
//            return false
//        }
//        val srcFiles = srcDir.listFiles()
//
//        for (i in srcFiles.indices) {
//            if (srcFiles[i].isFile) {
//                val destFile = File(destDir.absolutePath, srcFiles[i].name)
//                copyFileTo(srcFiles[i], destFile)
//            } else {
//                val theDestDir = File(destDir.absolutePath, srcFiles[i].name)
//                copyFilesTo(srcFiles[i], theDestDir)
//            }
//
//        }
//        return true
//    }

    /**
     * 移动一个文件
     * srcFile源文件
     * destFile目标文件
     */
//    fun moveFileTo(srcFile: File, destFile: File): Boolean {
//        if (!srcFile.exists() || srcFile.isDirectory || destFile.isDirectory) {
//            return false
//        }
//        val iscopy = copyFileTo(srcFile, destFile)
//        if (!iscopy) {
//            return false
//        } else {
//            delFile(srcFile)
//            return true
//        }
//    }

    /**
     * 移动目录下的所有文件到指定目录
     * srcDir 原路径
     * destDir 目标路径
     */
//    fun moveFilesTo(srcDir: File, destDir: File): Boolean {
//        if (!srcDir.exists() || !srcDir.isDirectory || !destDir.isDirectory) {
//            return false
//        }
//
//        val srcDirFiles = srcDir.listFiles()
//        for (i in srcDirFiles.indices) {
//            if (srcDirFiles[i].isFile) {
//                val oneDestFile = File(destDir.absolutePath, srcDirFiles[i].name)
//                moveFileTo(srcDirFiles[i], oneDestFile)
//            } else {
//                val oneDestFile = File(destDir.absolutePath, srcDirFiles[i].name)
//                moveFilesTo(srcDirFiles[i], oneDestFile)
//            }
//        }
//        return true
//    }

    /**
     * 文件转byte数组
     * file 文件路径
     */

//    @Throws(IOException::class)
//    fun file2byte(file: File?): ByteArray? {
//        var bytes: ByteArray? = null
//        if (file != null) {
//            val `is` = FileInputStream(file)
//            val length = file.length().toInt()
//            if (length > Integer.MAX_VALUE) {// 当文件的长度超过了int的最大值
//                println("this file is max ")
//                `is`.close()
//                return null
//            }
//            bytes = ByteArray(length)
//            var offset = 0
//            var numRead = 0
//            while (offset < bytes.size && (numRead = `is`.read(bytes, offset, bytes.size - offset)) >= 0) {
//                offset += numRead
//            }
//            `is`.close()
//            // 如果得到的字节长度和file实际的长度不一致就可能出错了
//            if (offset < bytes.size) {
//                println("file length is error")
//                return null
//            }
//        }
//        return bytes
//    }


    /**
     * 文件读取
     * filePath 文件路径
     */
//    fun readFile(filePath: File): String? {
//
//        var bufferedReader: BufferedReader? = null
//        val fileStr = StringBuilder()
//        if (!filePath.exists() || filePath.isDirectory) {
//            return null
//        }
//        try {
//            bufferedReader = BufferedReader(FileReader(filePath))
//            var tempFileStr = ""
//
//            while ((tempFileStr = bufferedReader.readLine()) != null) {
//                fileStr.append(tempFileStr)
//                fileStr.append("\n")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//        return fileStr.toString()
//
//    }

    /**
     * 文件读取
     * strPath 文件路径
     */
//    fun readFile(strPath: String): String? {
//        return readFile(File(strPath))
//    }

    /**
     * InputStream 转字符串
     */
//    fun readInp(inputStream: InputStream): String {
//        val outputStream = ByteArrayOutputStream()
//        val buf = ByteArray(1024)
//        try {
//            var len1: Int
//            while ((len1 = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len1)
//            }
//            inputStream.close()
//            outputStream.close()
//        } catch (var5: IOException) {
//        }
//
//        return outputStream.toString()
//    }

    /**
     * InputStream转byte数组
     *
     * @param inputStream
     * @return
     */
//    fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        val buf = ByteArray(1024)
//        try {
//            var len1: Int
//            while ((len1 = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len1)
//            }
//            outputStream.close()
//            inputStream.close()
//        } catch (var5: IOException) {
//        }
//
//        return outputStream.toByteArray()
//    }

    /**
     * BufferedReader 转字符串
     */
//    fun readBuff(bufferedReader: BufferedReader?): String? {
//        var readerstr = ""
//        try {
//            var tempstr = ""
//            while ((tempstr = bufferedReader!!.readLine()) != null) {
//                readerstr += tempstr
//            }
//            return readerstr
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//    }

    /**
     * InputStream转文件
     *
     * @param inputStream
     * @param absPath
     */
//    fun inputStreamToFile(inputStream: InputStream, absPath: String): Boolean {
//        var fos: FileOutputStream? = null
//        try {
//            fos = FileOutputStream(absPath, false)
//            fos.write(inputStreamToByteArray(inputStream))
//            return true
//        } catch (var7: IOException) {
//            var7.printStackTrace()
//            return false
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//
//    }

    /**
     * 文件转InputStream
     *
     * @param absPath
     * @return
     */
    fun file2Inp(absPath: String): InputStream? {
        val file = File(absPath)
        //        FLogUtils.getInstance().e(file.length());
        if (!file.exists()) {
            return null
        }
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            return `is`
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }


    }

    /**
     * 写入数据到文件
     *
     * @param filePath
     * @param content
     * @return
     */
    fun writeText(filePath: File, content: String): Boolean {
        creatFile(filePath)
        var bufferedWriter: BufferedWriter? = null
        try {
            bufferedWriter = BufferedWriter(FileWriter(filePath))
            bufferedWriter.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return true
    }

    fun writeText(filePath: String, content: String): Boolean {
        return writeText(File(filePath), content)
    }


    /**
     * byte数组转文件
     *
     * @param content
     * @param file_path
     */
    fun writeByteArrayToFile(content: ByteArray, file_path: String): Boolean {
        try {
            val file = File(file_path)
            val fileW = FileOutputStream(file.canonicalPath)
            fileW.write(content)
            fileW.close()
        } catch (var4: Exception) {
            return false
        }

        return true
    }


    /**
     * 追加数据
     *
     * @param filePath
     * @param content
     * @return
     */
    fun appendText(filePath: File, content: String): Boolean {
        creatFile(filePath)
        var writer: FileWriter? = null
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = FileWriter(filePath, true)
            writer.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return true
    }


    /**
     * 追加数据
     *
     * @param filePath
     * @param content
     * @param header   是否在头部追加数据
     */
//        fun appendText(filePath: String, content: String, header: Boolean) {
//            var raf: RandomAccessFile? = null
//            var tmpOut: FileOutputStream? = null
//            var tmpIn: FileInputStream? = null
//            try {
//                val tmp = File.createTempFile("tmp", null)
//                tmp.deleteOnExit()//在JVM退出时删除
//
//                raf = RandomAccessFile(filePath, "rw")
//                //创建一个临时文件夹来保存插入点后的数据
//                tmpOut = FileOutputStream(tmp)
//                tmpIn = FileInputStream(tmp)
//                var fileLength: Long = 0
//                if (!header) {
//                    fileLength = raf.length()
//                }
//                raf.seek(fileLength)
//                /**将插入点后的内容读入临时文件夹 */
//
//                val buff = ByteArray(1024)
//                //用于保存临时读取的字节数
//                var hasRead = 0
//                //循环读取插入点后的内容
//                while ((hasRead = raf.read(buff)) > 0) {
//                    // 将读取的数据写入临时文件中
//                    tmpOut.write(buff, 0, hasRead)
//                }
//                //插入需要指定添加的数据
//                raf.seek(fileLength)//返回原来的插入处
//                //追加需要追加的内容
//                raf.write(content.toByteArray())
//                //最后追加临时文件中的内容
//                while ((hasRead = tmpIn.read(buff)) > 0) {
//                    raf.write(buff, 0, hasRead)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                if (tmpOut != null) {
//                    try {
//                        tmpOut.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//
//                if (tmpIn != null) {
//                    try {
//                        tmpIn.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//                if (raf != null) {
//                    try {
//                        raf.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//            }
//        }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    fun getLength(filePath: File): Long {
        return if (!filePath.exists()) {
            -1
        } else {
            filePath.length()
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    fun getLength(filePath: String): Long {
        return getLength(File(filePath))
    }

    /**
     * 获取文件名
     *
     * @param filePath
     * @return
     */
    fun getFileName(filePath: String): String? {
        val file = File(filePath)
        return if (!file.exists()) {
            null
        } else file.name

    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    fun exists(filePath: String): Boolean {
        return File(filePath).exists()
    }


    /**
     * 按文件时间排序
     *
     * @param fliePath
     * @param desc
     * @return
     */
    fun orderByDate(fliePath: File, desc: Boolean): Array<File?> {
        val fs = fliePath.listFiles()
        Arrays.sort(fs, object : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                val diff = f1.lastModified() - f2.lastModified()
                return if (diff > 0)
                    1
                else if (diff == 0L)
                    0
                else
                    -1
            }

            override fun equals(other: Any?): Boolean {
                return true
            }

        })
        return if (desc) {
            val nfs = arrayOfNulls<File>(fs.size)
            for (i in fs.size - 1 downTo -1 + 1) {
                nfs[fs.size - 1 - i] = fs[i]
            }
            nfs
        } else {
            fs
        }
    }

    /**
     * 按照文件名称排序
     *
     * @param fliePath
     * @param desc
     * @return
     */
    fun orderByName(fliePath: File, desc: Boolean): Array<File?> {
        val files = fliePath.listFiles()
        Arrays.sort(files, Comparator { o1, o2 ->
            if (o1.isDirectory && o2.isFile)
                return@Comparator -1
            if (o1.isFile && o2.isDirectory) 1 else o1.name.compareTo(o2.name)
        })

        return if (desc) {
            val nfs = arrayOfNulls<File>(files.size)
            for (i in files.size - 1 downTo -1 + 1) {
                nfs[files.size - 1 - i] = files[i]
            }
            nfs
        } else {
            files
        }

    }

    /**
     * 按照文件大小排序
     *
     * @param fliePath
     */
    fun orderByLength(fliePath: File, desc: Boolean): Array<File?> {
        val files = fliePath.listFiles()
        Arrays.sort(files, object : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                val diff = f1.length() - f2.length()
                return when {
                    diff > 0 -> 1
                    diff == 0L -> 0
                    else -> -1
                }
            }

            override fun equals(obj: Any?): Boolean {
                return true
            }
        })

        return if (desc) {
            val nfs = arrayOfNulls<File>(files.size)
            for (i in files.size - 1 downTo -1 + 1) {
                nfs[files.size - 1 - i] = files[i]
            }
            nfs
        } else {
            files
        }
    }


    /**
     * 文件筛选
     *
     * @param files
     * @param filter
     * @return
     */
    fun filter(files: Array<File>?, filter: String): List<File> {
        val filels = ArrayList<File>()
        if (files != null) {
            for (i in files.indices) {
                if (files[i].name.contains(filter)) {
                    filels.add(files[i])
                }
            }
        }
        return filels
    }

    /**
     * 文件筛选
     *
     * @param file
     * @param filterName 筛选名
     * @return
     */
    fun fileNameFilter(file: File, filterName: String): Array<File>? {
        return if (!file.isDirectory) {
            null
        } else file.listFiles { pathname ->
            pathname.name.contains(filterName)
        }

    }

    /**
     * 获取文件列表
     *
     * @param fileDir 文件目录
     */
    fun getFiles(fileDir: String): Array<File>? {
        return getFiles(File(fileDir))
    }

    /**
     * 获取文件列表
     *
     * @param fileDir 文件目录
     */
    fun getFiles(fileDir: File): Array<File>? {
        return if (!fileDir.isDirectory) {
            null
        } else fileDir.listFiles()
    }
}