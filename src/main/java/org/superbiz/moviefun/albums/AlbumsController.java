package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        saveUpload(uploadedFile, albumId);
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException
    {
        Optional<Blob> optionalBlob = blobStore.get(Long.toString(albumId));
        Blob blob = optionalBlob.get();
        InputStream inputStream = blob.getInputStream();

        byte[] bytes = IOUtils.toByteArray(blob.getInputStream());

        HttpHeaders headers = createImageHttpHeaders(blob.getContentType(), bytes.length);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bytes, headers);
        return httpEntity;
    }


    private void saveUpload(@RequestParam("file") MultipartFile uploadedFile, long albumId) throws IOException
    {

        byte[] bytes = uploadedFile.getBytes();
        String contentType = new Tika().detect(bytes);

        Blob blob1 = new Blob(Long.toString(albumId), uploadedFile.getInputStream(), contentType);
        blobStore.put(blob1);
    }


    private HttpHeaders createImageHttpHeaders(String contentType, long contentLength)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        return headers;
    }
}
