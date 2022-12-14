package top.xinsin.util;

import lombok.extern.slf4j.Slf4j;
import top.xinsin.download.FabricVersion;
import top.xinsin.download.VillagerDownload;
import top.xinsin.download.VillagerVersion;
import top.xinsin.entity.FabricLoaderVersionEntity;
import top.xinsin.entity.VillagerVersionEntity;
import top.xinsin.entity.XMTLEntity;
import top.xinsin.minecraft.GetVersions;
import top.xinsin.minecraft.LaunchMinecraft;
import top.xinsin.minecraft.MicrosoftLogin;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created On 8/3/22 11:41 AM
 *
 * @author xinsin
 * @version 1.0.0
 */
@Slf4j
public class InputUtil {
    private static final GetVersions versions = new GetVersions();
    public static String minecraftPath = null;
    public static String minecraft_libraries = null;
    static {
        minecraftPath = FileUtil.readConfigureFile().getMinecraftPath();
        minecraft_libraries = minecraftPath + "libraries/";
    }
    public static void userInputHandler(String input){
        if (input.charAt(0) == '~'){
            String[] s = input.split(" ");
            String content = s[0].substring(1);
            new Thread(() ->{
                Thread.currentThread().setName("Input");
                switch (content){
                    case "exit":
                        inputExit();
                        break;
                    case "help":
                        inputHelp();
                        break;
                    case "versions":
                        inputVersions();
                        break;
                    case "launch":
                        inputLaunch(s);
                        break;
                    case "login":
                        inputLogin(s);
                        break;
                    case "refresh":
                        inputRefresh();
                        break;
                    case "java":
                        inputFindJava(s);
                        break;
                    case "wrap":
                        inputWrapCommand(s);
                        break;
                    case "minecraft":
                        inputMinecraftPath(s);
                        break;
                    case "game":
                        inputGame(s);
                        break;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }).start();
        }else {
            System.out.println("unknown command");
        }
    }

    private static void inputGame(String[] s) {
        if(s.length == 1){
            log.info("?????????~game <villager|fabric|forge|optifine> ?????????????????????");
        }else if (s.length >= 2){
            switch (s[1]){
                case "villager":
                    if (s.length >= 3) {
                        String type = s[2];
                        switch (type) {
                            case "release":
                                if (s.length == 4){
                                    VillagerVersionEntity versionEntity = VillagerVersion.releaseVersions.get(Integer.parseInt(s[3]) - 1);
                                    new VillagerDownload().villagerVersionJSONDownload(versionEntity.getUrl(),versionEntity.getId());
                                }else{
                                    log.info("\n" + VillagerVersion.getReleaseVersions());
                                }
                                break;
                            case "snapshot":
                                if (s.length == 4){
                                    VillagerVersionEntity versionEntity = VillagerVersion.releaseVersions.get(Integer.parseInt(s[3]) - 1);
                                    new VillagerDownload().villagerVersionJSONDownload(versionEntity.getUrl(),versionEntity.getId());
                                }else {
                                    log.info("\n" + VillagerVersion.getSnapshotVersions());
                                }
                                break;
                            case "old_beta":
                                if (s.length == 4){
                                    VillagerVersionEntity versionEntity = VillagerVersion.releaseVersions.get(Integer.parseInt(s[3]) - 1);
                                    new VillagerDownload().villagerVersionJSONDownload(versionEntity.getUrl(),versionEntity.getId());
                                }else {
                                    log.info("\n" + VillagerVersion.getOldBetaVersions());
                                }
                                break;
                            default:
                                System.out.println("Unknown type");
                                break;
                        }
                    }else {
                        log.info("?????????~game villager <release|snapshot|old_beta> ?????????????????????");
                    }
                case "fabric":
                    inputFabric(s);
                    break;
                case "forge":
                    inputForge();
                    break;
                case "optifine":
                    inputOptifine();
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }

    private static void inputOptifine() {
    }

    private static void inputForge() {

    }

    private static void inputFabric(String[] s) {
        if (s.length == 2) {
//            ??????????????????
//            ???????????????arrayList??????
            VillagerVersion.getReleaseVersions();
            ArrayList<VillagerVersionEntity> releaseVersions = VillagerVersion.releaseVersions;
            for (int j = 0;j < releaseVersions.size();j++) {
                log.info("{}.\t{}\t{}",j + 1,releaseVersions.get(j).getId(),releaseVersions.get(j).getTime());
            }
            log.info("fabric?????????????????????????????????,?????????~game fabric <1,2,3,4,5...{}> ????????????????????????",VillagerVersion.releaseVersions.size());
        } else if (s.length >= 3) {
            if (s.length == 3){
                ArrayList<FabricLoaderVersionEntity> fabricVersion = FabricVersion.getFabricVersion(VillagerVersion.releaseVersions.get(Integer.parseInt(s[2]) - 1).getId());
                for (int j = fabricVersion.size() - 1; j >= 0 ; j--) {
                    log.info("{}.\t{}\t{}",j + 1,fabricVersion.get(j).getVersion(),fabricVersion.get(j).getStable());
                }
                log.info("fabric?????????????????????????????????,?????????~game fabric {????????????} <1,2,3,4,5...{}> ????????????????????????",fabricVersion.size());
            } else if (s.length == 4) {
                FabricVersion.getFabricLoader(VillagerVersion.releaseVersions.get(Integer.parseInt(s[2]) - 1).getId(),FabricVersion.getFabricVersion(VillagerVersion.releaseVersions.get(Integer.parseInt(s[2]) - 1).getId()).get(Integer.parseInt(s[3]) - 1).getVersion());
            }
        }else {
            log.info("?????????~game fabric ?????????????????????");
        }
    }

    private static void inputMinecraftPath(String[] s) {
        if (s.length == 1){
            log.info("?????????Minecraft??????,???:~minecraft </home/{user}/.minecraft/>");
        }else if (s.length == 2){
            XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
            xmtlEntity.setMinecraftPath(s[1]);
            FileUtil.writeConfigureFile(xmtlEntity);
            minecraftPath = s[1];
            log.info("????????????minecraft?????????:{}",s[1]);
        }else{
            log.info("Unknown command");
        }
    }

    private static void inputWrapCommand(String[] s) {
        if (s.length == 1){
            log.info("?????????~wrap <????????????>");
        } else if (s.length == 2) {
            log.info("????????????????????????:{}",s[1]);
            XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
            xmtlEntity.setWrapCommand(s[1]);
            FileUtil.writeConfigureFile(xmtlEntity);
            log.info("????????????");
        }
    }

    private static void inputFindJava(String[] s) {
        ArrayList<String> java = FindJava.findJava();
        if (s.length == 1){
            if (java.size() == 0){
                log.warn("No java found");
            }else {
                log.info("Found java:{}",java.size());
                java.forEach(log::info);
                log.info("?????????~java <index|name>?????????????????????java??????");
            }
        } else if (s.length == 2) {
            XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
            String pattern = "^\\d+$";
            if (s[1].matches(pattern)) {
                int index = Integer.parseInt(s[1]);
                if (index > java.size()) {
                    log.warn("index out of range");
                } else {
                    xmtlEntity.setSelectJavaVersion(java.get(index - 1));
                    log.info("set java version:{}", xmtlEntity.getSelectJavaVersion());
                }
            } else {
                String name = s[1];
                for (String java1 : java) {
                    if (java1.equals(name)) {
                        xmtlEntity.setSelectJavaVersion(java1);
                        log.info("set java version:{}", xmtlEntity.getSelectJavaVersion());
                        return;
                    }
                }
                log.warn("No java found");
            }
            FileUtil.writeConfigureFile(xmtlEntity);
        }else {
            log.warn("Unknown command");
        }

    }

    private static void inputRefresh() {
        log.info("????????????????????????");
        new MicrosoftLogin().accountRefresh();
        log.info("????????????,????????????????????????");
        log.info("????????????????????????,?????????~launch <version> ??????");
    }

    private static void inputLogin(String[] args) {
        if (args.length == 1){
            String url = "https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
            System.out.println(url);
//            ???????????????????????????
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable trans = new StringSelection(url);
            systemClipboard.setContents(trans,null);
            System.out.println("??????????????????????????????,???????????????????????????????????????????????????!");
            System.out.println("????????????????????????????????????????????????????????????");
            System.out.println("?????????~login <code>????????????");
        } else if (args.length == 2) {
            if (new MicrosoftLogin().login(args[1])){
                System.out.println("????????????,?????????~launch <version>????????????");
            }else {
                System.out.println("????????????,?????????????????????");
            }
        }else {
            System.out.println("incorrect number of parameters");
        }
    }

    private static void launchGame(String s){
        XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
        if (xmtlEntity.getMinecraftPath() == null) {
            log.warn("?????????Minecraft??????,???:~minecraft </home/{user}/.minecraft/>");
        }else {
            Map<String, String> minecraftVersions = versions.getMinecraftVersions(minecraftPath);
            Set<String> keys = minecraftVersions.keySet();
            int num = 0;
            for (String key : keys) {
                if (s.equals(key)) {
                    new LaunchMinecraft().readVersionJson(minecraftVersions.get(key));
                    xmtlEntity.setBeforeLaunch(s);
                    FileUtil.writeConfigureFile(xmtlEntity);
                } else {
                    num++;
                    if (num == keys.size()) {
                        System.out.println("unknown version");
                        num = 0;
                    }
                }
            }
        }
    }
    private static void inputLaunch(String[] args) {
        if (args.length == 1){
            XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
            if (xmtlEntity.getBeforeLaunch() != null){
                launchGame(xmtlEntity.getBeforeLaunch());
            }else {
                log.info("?????????~launch <version>????????????");
                log.info("????????????????????????,???????????? ~launch ?????????????????????");
            }
        }else if (args.length == 2) {
            launchGame(args[1]);
        }else {
            log.warn("????????????");
        }
    }

    private static void inputVersions() {
        XMTLEntity xmtlEntity = FileUtil.readConfigureFile();
        if (xmtlEntity.getMinecraftPath() == null) {
            log.warn("?????????Minecraft??????,???:~minecraft </home/{user}/.minecraft/>");
        }else {
            try {
                Map<String, String> minecraftVersions = versions.getMinecraftVersions(minecraftPath);
                if (minecraftVersions.size() == 0) {
                    log.info("????????????minecraft??????");
                } else {
                    log.info("?????? " + minecraftVersions.size() + "???minecraft??????");
                    minecraftVersions.keySet().forEach(log::info);
                }
            }catch (Exception e){
                log.warn("??????????????????minecraft??????,????????? ~game ???????????????");
            }
        }
    }

    private static void inputHelp() {
        log.info("~help: ????????????");
        log.info("~exit: ????????????");
        log.info("~java: ??????java??????");
        log.info("~minecraft: ??????minecraft??????");
        log.info("~game: ??????minecraft");
        log.info("~login: ????????????");
        log.info("~launch: ??????minecraft");
        log.info("~refresh: ??????????????????");
        log.info("~versions: ????????????minecraft??????");
    }

    private static void inputExit(){
        log.info("??????!");
        System.exit(0);
    }

}
