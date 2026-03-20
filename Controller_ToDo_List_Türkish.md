# Controller ToDo Listesi

Bu dokümantasyon, projedeki 3 ana kullanıcı rolü (Student, Betreuer, Admin) için gerekli Controller endpoint'lerini listeler.

## Mevcut Durum

### WebController
- Genel endpoint'ler (login, addProfile, addTopics, addusers, homestudi, admin)
- Bazı endpoint'ler ilgili Controller'lara taşınmalı

### BetreuerController
- ✅ GET /betreuerHome - Betreuer ana sayfası
- ✅ GET /betreuerHome/deleteTopic/{id} - Topic silme
- ✅ GET /betreuerHome/editTopic/{id} - Topic düzenleme sayfası
- ❌ POST /betreuerHome/editTopic/{id} - Topic düzenleme işlemi
- ❌ GET /betreuerHome/editProfile - Profil düzenleme sayfası
- ❌ POST /betreuerHome/editProfile - Profil düzenleme işlemi
- ❌ POST /betreuerHome/uploadDocument - Doküman yükleme
- ❌ GET /betreuerHome/deleteDocument/{id} - Doküman silme
- ❌ POST /betreuerHome/addTag - Tag ekleme
- ❌ GET /betreuerHome/deleteTag - Tag silme
- ❌ GET /betreuerHome/acceptTopic/{id} - Topic kabul etme
- ❌ GET /betreuerHome/declineTopic/{id} - Topic reddetme
- ❌ GET /betreuerHome/addTopics - Topic ekleme sayfası (WebController'dan taşınmalı)
- ❌ POST /betreuerHome/addTopics - Topic ekleme işlemi (WebController'dan taşınmalı)

---

## 1. BetreuerController - Eksik Endpoint'ler

### Yüksek Öncelik

#### 1.1 Topic Yönetimi
- [ ] **POST /betreuerHome/editTopic/{id}**
  - TopicForm ile topic güncelleme
  - Validation kontrolü
  - Redirect: /betreuerHome

#### 1.2 Profil Yönetimi
- [ ] **GET /betreuerHome/editProfile**
  - EditProfile sayfası göster
  - Mevcut profil bilgilerini model'e ekle
  - Template: editProfile.html

- [ ] **POST /betreuerHome/editProfile**
  - ProfileForm ile profil güncelleme
  - Validation kontrolü
  - Redirect: /betreuerHome

#### 1.3 Tag Yönetimi
- [ ] **POST /betreuerHome/addTag**
  - RequestParam: selectedTag
  - Betreuer profilindeki tags'e ekle
  - Redirect: /betreuerHome

- [ ] **GET /betreuerHome/deleteTag**
  - RequestParam: tagToDelete
  - Betreuer profilindeki tags'den sil
  - Redirect: /betreuerHome

#### 1.4 Topic Onay/Red İşlemleri
- [ ] **GET /betreuerHome/acceptTopic/{id}**
  - Topic'i kabul et
  - Status güncelle (TopicForm'a status field eklenmeli)
  - Redirect: /betreuerHome

- [ ] **GET /betreuerHome/declineTopic/{id}**
  - Topic'i reddet
  - Status güncelle
  - Redirect: /betreuerHome

### Orta Öncelik

#### 1.5 Doküman Yönetimi
- [ ] **POST /betreuerHome/uploadDocument**
  - MultipartFile ile doküman yükleme
  - DocumentForm kullan
  - Dosya validasyonu (izin verilen tipler)
  - Redirect: /betreuerHome

- [ ] **GET /betreuerHome/deleteDocument/{id}**
  - Doküman silme
  - Redirect: /betreuerHome

#### 1.6 Topic Ekleme (WebController'dan Taşınmalı)
- [ ] **GET /betreuerHome/addTopics**
  - TopicForm'u model'e ekle
  - Template: add-topics.html
  - WebController'daki /addTopics endpoint'ini kaldır

- [ ] **POST /betreuerHome/addTopics**
  - TopicForm validation
  - TopicService.addTopic() çağrısı
  - Redirect: /betreuerHome (şu an /addTopics'a redirect ediyor, değiştirilmeli)

### Düşük Öncelik

#### 1.7 Model Attribute'ları
- [ ] **betreuerHome() metoduna eklenmeli:**
  - `allTags` - Tüm mevcut tag'lerin listesi (TagService gerekli)
  - `documents` - Betreuer'a ait dokümanlar (DocumentService gerekli)
  - `documentForm` - Doküman yükleme formu (GET için)

---

## 2. StudentController - Oluşturulmalı

### Yüksek Öncelik

#### 2.1 Ana Sayfa
- [ ] **GET /homestudi**
  - Module ve tag listelerini model'e ekle
  - Template: roles/homestudi.html
  - WebController'dan taşınmalı

- [ ] **POST /homestudi**
  - Betreuer matching işlemi
  - mode=1 set et
  - Matching sonuçlarını model'e ekle
  - Template: roles/homestudi.html
  - WebController'dan taşınmalı

- [ ] **POST /homestudiThemen**
  - Topic matching işlemi
  - mode=2 set et
  - Matching sonuçlarını model'e ekle
  - Template: roles/homestudi.html
  - WebController'dan taşınmalı

#### 2.2 Profil Görüntüleme
- [ ] **GET /profil/{id}**
  - Betreuer veya Topic profilini göster
  - Template: profil.html (oluşturulmalı)

### Orta Öncelik

#### 2.3 Topic Başvurusu
- [ ] **POST /homestudi/applyTopic/{id}**
  - Student'in topic'e başvurması
  - Status güncelle
  - Redirect: /homestudi

---

## 3. AdminController - Oluşturulmalı

### Yüksek Öncelik

#### 3.1 Ana Sayfa
- [ ] **GET /admin**
  - Tüm kullanıcıları listele
  - UserService.getAllUsers() kullan
  - Template: roles/admin.html
  - WebController'dan taşınmalı

#### 3.2 Kullanıcı Yönetimi
- [ ] **GET /admin/addusers**
  - AddUserForm'u model'e ekle
  - Template: addusers.html
  - WebController'dan taşınmalı

- [ ] **POST /admin/addusers**
  - AddUserForm validation
  - UserService.createUserFromForm() çağrısı
  - Redirect: /admin
  - WebController'dan taşınmalı

- [ ] **GET /admin/editUser/{id}**
  - Kullanıcı düzenleme sayfası
  - Mevcut kullanıcı bilgilerini model'e ekle
  - Template: editUser.html (oluşturulmalı)

- [ ] **POST /admin/editUser/{id}**
  - Kullanıcı güncelleme
  - Validation kontrolü
  - Redirect: /admin

- [ ] **GET /admin/deleteUser/{id}**
  - Kullanıcı silme
  - Onay mesajı
  - Redirect: /admin

### Orta Öncelik

#### 3.3 Arama Fonksiyonu
- [ ] **GET /admin/search**
  - RequestParam: searchTerm
  - Kullanıcı arama
  - Sonuçları model'e ekle
  - Template: roles/admin.html (aynı sayfa)

---

## 4. Genel İyileştirmeler

### 4.1 WebController Temizliği
- [ ] `/addTopics` endpoint'lerini BetreuerController'a taşı
- [ ] `/homestudi` endpoint'lerini StudentController'a taşı
- [ ] `/admin` ve `/addusers` endpoint'lerini AdminController'a taşı
- [ ] `/addProfile` endpoint'i kalabilir (genel kullanım için)

### 4.2 Service Katmanı
- [ ] **TagService oluştur:**
  - getAllTags() - Tüm mevcut tag'leri döndür
  - addTagToProfile() - Profile'a tag ekle
  - removeTagFromProfile() - Profile'dan tag sil

- [ ] **DocumentService oluştur:**
  - uploadDocument() - Doküman yükle
  - getDocumentsByProfileId() - Profile'a ait dokümanları getir
  - deleteDocument() - Doküman sil

- [ ] **MatchingService oluştur:**
  - matchBetreuers() - Student ile Betreuer matching
  - matchTopics() - Student ile Topic matching

### 4.3 Form Sınıfları
- [ ] **ProfileForm'a eksik alanlar ekle:**
  - tel (String)
  - email (String)
  - building (String)
  - room (String)
  - VEYA BetreuerForm kullan ve ProfileForm'u genişlet

- [ ] **TopicForm'a eksik alanlar ekle:**
  - student (User reference veya String username)
  - status (enum: PENDING, ACCEPTED, DECLINED)

### 4.4 Template'ler
- [ ] **editProfile.html** - Doldurulmalı (şu an boş)
- [ ] **editTopic.html** - Doldurulmalı (şu an boş)
- [ ] **profil.html** - Oluşturulmalı (Betreuer/Topic profil görüntüleme)
- [ ] **editUser.html** - Oluşturulmalı (Admin kullanıcı düzenleme)

---

## 5. Öncelik Sırası

### Faz 1 (Kritik - Hemen Yapılmalı)
1. BetreuerController - POST /editTopic/{id}
2. BetreuerController - GET/POST /editProfile
3. BetreuerController - POST /addTag, GET /deleteTag
4. BetreuerController - Model attribute'ları (allTags, documents)
5. WebController'dan /addTopics endpoint'lerini BetreuerController'a taşı

### Faz 2 (Önemli - Kısa Vadede)
1. StudentController oluştur ve /homestudi endpoint'lerini taşı
2. AdminController oluştur ve /admin, /addusers endpoint'lerini taşı
3. BetreuerController - GET /acceptTopic/{id}, GET /declineTopic/{id}
4. TagService ve DocumentService oluştur

### Faz 3 (İyileştirme - Orta Vadede)
1. BetreuerController - POST /uploadDocument, GET /deleteDocument/{id}
2. StudentController - GET /profil/{id}
3. AdminController - GET/POST /editUser/{id}, GET /deleteUser/{id}
4. Template'leri doldur (editProfile, editTopic, profil, editUser)

### Faz 4 (Ek Özellikler - Uzun Vadede)
1. MatchingService oluştur ve implement et
2. AdminController - GET /admin/search
3. ProfileForm ve TopicForm'a eksik alanları ekle

---

## Notlar

- Tüm redirect'ler ilgili Controller'ın ana sayfasına yönlendirmeli
- Validation hatalarında form tekrar gösterilmeli
- Security implementasyonu sonrası endpoint'lere @PreAuthorize eklenebilir
- Tüm endpoint'ler için test yazılmalı (WebControllerTest benzeri)
