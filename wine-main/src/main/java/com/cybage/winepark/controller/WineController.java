package com.cybage.winepark.controller;

import com.cybage.winepark.service.WineService;
import com.cybage.winepark.domain.Wine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cybage.winepark.service.WineServiceImpl;
import com.cybage.winepark.dto.WineDto;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@RequestMapping("wine")
@Slf4j
@AllArgsConstructor
public class WineController {
    WineService wineService;
    WineServiceImpl wineServiceImpl;
    static final String HEADERS = "Host Info";

    @GetMapping("")
    public ResponseEntity<String> getWineHome() {
        String htmlResponse = "<html><head><style>" +
                              "body { display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }" +
                              "p { font-size: 3em; }" + 
                              "</style></head><body><p><strong>Welcome to Wine Shop &#127867 !</strong></p></body></html>";
        return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
    }

    @GetMapping("getAllWines")
    public ResponseEntity<List<Wine>> getAllWines() {
        log.info("CONTROLLER: getAllWines");
        List<Wine> wines = wineService.getAllWines();

        String zone = getMetadata("instance/zone");
        String vpcId = getMetadata("instance/network-interfaces/0/network");
        String subnetId = getMetadata("instance/network-interfaces/0/subnet");
        String hostName = getMetadata("instance/name");
        String hostIp = getMetadata("instance/network-interfaces/0/ip");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Instance-Zone", zone);
        headers.add("X-VPC-ID", vpcId);
        headers.add("X-Subnet-ID", subnetId);
        headers.add("X-HOST-NAME", hostName);
        headers.add("X-HOST-IP", hostIp);


        return new ResponseEntity<>(wines,headers, HttpStatus.OK);
    }

    @GetMapping("getWineById/{id}")
    public  ResponseEntity<Wine> getWineById(@PathVariable("id") Integer id) {
        log.info("CONTROLLER: getWineById");
        Wine wine = wineService.getWineById(id);
        String osInfo= wineServiceImpl.getOperatingSystemInfo();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADERS, osInfo);
        return new ResponseEntity<>(wine,headers, HttpStatus.OK);
    }

    @PostMapping("add")
    public  ResponseEntity<String> addWine(@RequestBody WineDto wineDto) {
        log.info("CONTROLLER: addWine");
        Wine wine=wineServiceImpl.wineDtoToWine(wineDto);
        wineService.addWine(wine);
        String osInfo= wineServiceImpl.getOperatingSystemInfo();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADERS, osInfo);
        return new ResponseEntity<>("wine added successfully...",headers, HttpStatus.OK);
    }

    @PutMapping("updateWine/{id}")
    public  ResponseEntity<String> updateWine(@PathVariable("id") Integer id,@RequestBody WineDto wineDto) {
        log.info("CONTROLLER: updateWine");
        Wine wine=wineServiceImpl.wineDtoToWine(wineDto);
        wine.setWineId(id);
        wineService.updateWine(wine);
        String osInfo= wineServiceImpl.getOperatingSystemInfo();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADERS, osInfo);
        return new ResponseEntity<>("wine updated successfully...",headers, HttpStatus.OK);
    }



    @DeleteMapping("deleteWine/{id}")
    public  ResponseEntity<String> deleteWine(@PathVariable("id") Integer id) {
        log.info("CONTROLLER: deleteWine");
        wineService.deleteWine(id);
        String osInfo= wineServiceImpl.getOperatingSystemInfo();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADERS, osInfo);
        return new ResponseEntity<>("wine deleted successfully...",headers, HttpStatus.OK);
    }


    private String getMetadata(String path) {
        String metadataUrl = "http://metadata.google.internal/computeMetadata/v1/" + path;

        try {
            URL url = new URL(metadataUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Metadata-Flavor", "Google");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();
                return response;
            } else {
                // Handle error response
            }
        } catch (Exception e) {
            // Handle exception
        }

        return null;
    }
}






