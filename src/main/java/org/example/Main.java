package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] argv)  {
        //ArgsTest args = new ArgsTest("multi-thread", 4, "https://img.freepik.com/free-photo/hands-holding-money-bank-card-showing-thumb-up_107791-16993.jpg?t=st=1732053867~exp=1732057467~hmac=f1b635e459245e68e690ad9c82c437a2e96dc90ff6a0accfe9ed29297f6868ec&w=1060;https://img.freepik.com/free-photo/render-white-hand-put-coin-wallet-black-hand_107791-17464.jpg?t=st=1732053892~exp=1732057492~hmac=080641a83981c4238d7017f1997a6fbd69cfe455361eed4d9d12d593e92494d4&w=900;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740", "C:\\Users\\limag\\OneDrive\\Рабочий стол\\images");
        //     ArgsTest args = new ArgsTest("one-thread", 3, "https://img.freepik.com/free-photo/hands-holding-money-bank-card-showing-thumb-up_107791-16993.jpg?t=st=1732053867~exp=1732057467~hmac=f1b635e459245e68e690ad9c82c437a2e96dc90ff6a0accfe9ed29297f6868ec&w=1060;https://img.freepik.com/free-photo/render-white-hand-put-coin-wallet-black-hand_107791-17464.jpg?t=st=1732053892~exp=1732057492~hmac=080641a83981c4238d7017f1997a6fbd69cfe455361eed4d9d12d593e92494d4&w=900;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740;https://img.freepik.com/free-photo/3d-render-gift-box-with-ribbon-present-package_107791-14916.jpg?t=st=1732053933~exp=1732057533~hmac=8aa171b1f37bb01a3667df4d489439a2f91f702ee2a6d74654377597efbc6c2a&w=740", "C:\\Users\\limag\\IdeaProjects\\BackEnd2Course\\tbank_java_2024");
        Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        List<String> urls = getUrls(args.files);

        if(args.mode.equals("multi-thread")){

            try {
                var startTime = System.currentTimeMillis();

                businessLogicMulti(urls, args.folder, args.count);

                var endTime = System.currentTimeMillis();

                System.out.println(endTime - startTime + " ms");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else if (args.mode.equals("one-thread")) {
            try {
                var startTime = System.currentTimeMillis();

                businessLogicMulti(urls, args.folder, 1);

                var endTime = System.currentTimeMillis();

                System.out.println(endTime - startTime + " ms");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void businessLogicMulti(List<String> urls, String FILE_NAME, int nThread) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        List<Future<String>> futures = new ArrayList<>();
        int i = 1;
        for (String url : urls) {
            DownloadPicTask downloadTask = new DownloadPicTask(url, FILE_NAME, i);
            futures.add(executorService.submit(downloadTask));
            i++;
        }

        for (var f : futures) {
            while (!f.isDone()) {
                Thread.sleep(100);
            }
        }

        executorService.shutdown();

    }

    public static class DownloadPicTask implements Callable<String> {
        private String url_file;
        private String file_name;
        private int index;

        public DownloadPicTask(String url_file, String file_name, int index ) {
            this.url_file = url_file;
            this.index = index;
            this.file_name = file_name;
        }

        @Override
        public String call() throws Exception {
            pictureDownloading(url_file, file_name, index);
            System.out.println("Картинка скопировалась в " + file_name +"\\" + index + ".png");
            return "Картинка скопировалась в " + file_name +"\\" + index + ".png";

        }
    }
    public static void pictureDownloading(String FILE_URL, String FILE_NAME, int index) throws IOException {
        InputStream in = new URL(FILE_URL).openStream();
        Files.copy(in, Paths.get(FILE_NAME  +"\\"+ index + ".png"), StandardCopyOption.REPLACE_EXISTING);
    }
    public static List<String> getUrls(String EnteredUrls){
        List<String> urlsList = new ArrayList<>();
        String[] urlsArray = EnteredUrls.split("!");
        for (String url : urlsArray) {
            urlsList.add(url);
        }
        return urlsList;

    }
}
