package code.service.more;

import code.model.more.Image;
import code.repository.ImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

  private ImageRepository imageRepository;
  private final Cloudinary cloudinary;

  public ImageService(ImageRepository imageRepository, Cloudinary cloudinary) {
    this.imageRepository = imageRepository;
    this.cloudinary = cloudinary;
  }

  //  Lưu ảnh vào thư mục và đường dẫn vào database
  public String saveImage(MultipartFile file,String imageId,String tag ) throws IOException {

    Map<String, Object> options = ObjectUtils.asMap(
        "public_id",imageId,      // Đặt public ID cho ảnh
        "folder", "images",                  // Chỉ định thư mục lưu trữ
        "tags", tag                // Thêm các tag (danh sách thẻ)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
    return uploadResult.get("url").toString(); //
  }
}
