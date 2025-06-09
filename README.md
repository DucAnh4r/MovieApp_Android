
# MovieApp Android

MovieApp là một ứng dụng Android cho phép người dùng tìm kiếm, xem thông tin và xem các bộ phim. Ứng dụng sử dụng API từ kkPhim để lấy dữ liệu phim, bao gồm tiêu đề, mô tả, hình ảnh, đánh giá và phim. Giao diện người dùng được thiết kế thân thiện và dễ sử dụng, giúp người dùng dễ dàng tìm kiếm và khám phá các bộ phim mới.


## Công nghệ sử dụng

Ngôn ngữ lập trình: Java
- **Android SDK:** Tối thiểu 21 (Android 5.0 Lollipop)
Thư viện chính:
- **Firebase:** Lưu trữ dữ liệu, xác thực người dùng, và phân tích.
- **Facebook SDK:** Tích hợp đăng nhập và các tính năng của Facebook.
- **Google Services:** Xác thực người dùng qua Google.
- **ExoPlayer:** Phát video.
- **Retrofit:** Thực hiện yêu cầu HTTP và xử lý JSON.
- **Glide:** Tải và hiển thị hình ảnh.
- **Material Design:** Thiết kế giao diện người dùng hiện đại.


## Tính năng nổi bật

- **Tìm kiếm phim:** Người dùng có thể tìm kiếm các bộ phim theo tên.
- **Xem thông tin chi tiết:** Hiển thị thông tin chi tiết về bộ phim, bao gồm mô tả, và hình ảnh.
- **Xem phim:** Người dùng có thể phát và xem các bộ phim trực tiếp trong ứng dụng.
- **Thêm vào danh sách yêu thích:** Cho phép người dùng thêm bộ phim vào danh sách yêu thích từ trang thông tin chi tiết.
- **Thêm vào danh sách xem sau:** Cho phép người dùng thêm bộ phim vào danh sách xem sau từ trang thông tin chi tiết.
- **Giao diện thân thiện:** Thiết kế giao diện người dùng theo tiêu chuẩn Material Design.
- **Lịch sử tìm kiếm:** Lưu lại lịch sử tìm kiếm của người dùng để dễ dàng truy cập lại.
- **Đăng nhập bằng Google:** Người dùng có thể đăng nhập vào ứng dụng bằng tài khoản Google của họ.
- **Đăng nhập bằng Facebook:** Người dùng có thể đăng nhập vào ứng dụng bằng tài khoản Facebook của họ.
## Hướng dẫn cài đặt

Bước 1: Clone repository
```bash
git clone https://github.com/DucAnh4r/MovieApp_Android.git
```
Bước 2: Mở dự án trong Android Studio
- Mở Android Studio và chọn "Open an existing Android Studio project".
- Chọn thư mục chứa dự án đã clone.
Bước 3: Cấu hình API Key
- Đăng ký tài khoản tại Firebase để tạo dự án và lấy thông tin cấu hình.
- Tải tệp google-services.json từ Firebase Console và thêm vào thư mục app/ trong dự án.
- Đăng ký tài khoản tại Facebook for Developers để tạo ứng dụng và lấy Facebook App ID và App Secret.
- Thêm Facebook App ID vào tệp strings.xml trong thư mục res/values/ như sau:
```bash
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```
Bước 3: Chạy ứng dụng
- Kết nối thiết bị Android hoặc sử dụng trình giả lập.
- Nhấn nút "Run" trong Android Studio để biên dịch và chạy ứng dụng.
## Ảnh demo
- **Trang chủ**
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218487/3bf61041-de21-459b-82f5-7b5cb727b0f5_jblkdb.jpg)

- **Trang profile**
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218485/219345aa-25ed-464c-900e-c3bc0a2772f5_hvdzud.jpg)

- **Trang chi tiết**
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218487/dcbfe27a-3cfb-46c3-a342-788ef323b156_xctqrl.jpg)

- **Trang xem phim**
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218485/a68fb034-e2f1-4e64-9c75-d4a44710556f_i2fvog.jpg)

## Link tải app

https://drive.google.com/drive/folders/1KGqk0wwrvtfWGBAPDBcADd0brchOPztt


## Hỗ trợ

Để được hỗ trợ, vui lòng gửi email đến manhduc889@gmail.com.
