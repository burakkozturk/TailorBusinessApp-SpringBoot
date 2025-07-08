package erdalguda.main.service;

import erdalguda.main.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username:noreply@erdalguda.com}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderStatusUpdateEmail(Order order) {
        log.info("Sipariş durum güncelleme email'i gönderme işlemi başlatıldı. Sipariş ID: {}", order.getId());
        
        if (order.getCustomer() == null) {
            log.warn("Sipariş müşteri bilgisi null. Sipariş ID: {}", order.getId());
            return;
        }
        
        if (order.getCustomer().getEmail() == null || order.getCustomer().getEmail().trim().isEmpty()) {
            log.info("Müşteri email adresi bulunamadı, email gönderilmiyor. Sipariş ID: {}, Müşteri: {} {}", 
                    order.getId(), 
                    order.getCustomer().getFirstName(), 
                    order.getCustomer().getLastName());
            return;
        }

        try {
            log.info("Email gönderim parametreleri - From: {}, To: {}, Subject: Sipariş Durumu Güncellendi", 
                    fromEmail, order.getCustomer().getEmail());
                    
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Sipariş Durumu Güncellendi - Erdal Güda Terzilik");
            message.setText(buildOrderStatusMessage(order));

            log.info("MailSender ile email gönderiliyor...");
            mailSender.send(message);
            
            log.info("✅ Sipariş durum güncelleme emaili BAŞARIYLA gönderildi! Müşteri: {}, Email: {}, Durum: {}", 
                    order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                    order.getCustomer().getEmail(),
                    order.getStatus().getDisplayName());
                    
        } catch (Exception e) {
            log.error("❌ Email gönderilirken hata oluştu. Sipariş ID: {}, Email: {}, Hata: {}", 
                    order.getId(), 
                    order.getCustomer().getEmail(),
                    e.getMessage(), e);
        }
    }

    private String buildOrderStatusMessage(Order order) {
        StringBuilder message = new StringBuilder();
        
        message.append("Sayın ").append(order.getCustomer().getFirstName())
                .append(" ").append(order.getCustomer().getLastName()).append(",\n\n");
                
        message.append("Siparişinizin durumu güncellendi.\n\n");
        
        message.append("Sipariş Detayları:\n");
        message.append("- Sipariş Numarası: ").append(order.getId()).append("\n");
        message.append("- Ürün Tipi: ").append(order.getProductType().getDisplayName()).append("\n");
        message.append("- Kesim Tipi: ").append(order.getFitType().getDisplayName()).append("\n");
        message.append("- Yeni Durum: ").append(order.getStatus().getDisplayName()).append("\n");
        
        if (order.getEstimatedDeliveryDate() != null) {
            message.append("- Tahmini Teslim Tarihi: ").append(order.getEstimatedDeliveryDate()).append("\n");
        }
        
        if (order.getDeliveryDate() != null) {
            message.append("- Teslim Tarihi: ").append(order.getDeliveryDate()).append("\n");
        }
        
        if (order.getNotes() != null && !order.getNotes().trim().isEmpty()) {
            message.append("- Notlar: ").append(order.getNotes()).append("\n");
        }
        
        message.append("\n");
        
        // Duruma göre özel mesajlar
        switch (order.getStatus()) {
            case PREPARING:
                message.append("Siparişiniz hazırlık aşamasındadır. Ölçüleriniz kontrol ediliyor.\n");
                break;
            case CUTTING:
                message.append("Siparişiniz kesim aşamasına geçmiştir. Kumaşlarınız kesiliyor.\n");
                break;
            case SEWING:
                message.append("Siparişiniz dikim aşamasındadır. Ürününüz dikiliyor.\n");
                break;
            case FITTING:
                message.append("Siparişiniz prova aşamasındadır. Prova randevunuz için sizinle iletişime geçeceğiz.\n");
                break;
            case READY:
                message.append("Siparişiniz hazır! Teslim alabilirsiniz.\n");
                break;
            case DELIVERED:
                message.append("Siparişiniz teslim edilmiştir. Bizi tercih ettiğiniz için teşekkür ederiz!\n");
                break;
            case CANCELLED:
                message.append("Siparişiniz iptal edilmiştir. Herhangi bir sorunuz için bizimle iletişime geçebilirsiniz.\n");
                break;
        }
        
        message.append("\nSorularınız için bizimle iletişime geçebilirsiniz.\n");
        message.append("\nErdal Güda Terzilik\n");
        message.append("Telefon: +90 555 555 55 55\n");
        message.append("Email: info@erdalguda.com\n");
        
        return message.toString();
    }

    public void sendWelcomeEmail(String email, String firstName, String lastName) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Hoş Geldiniz - Erdal Güda Terzilik");
            
            StringBuilder messageText = new StringBuilder();
            messageText.append("Sayın ").append(firstName).append(" ").append(lastName).append(",\n\n");
            messageText.append("Erdal Güda Terzilik ailesine hoş geldiniz!\n\n");
            messageText.append("Müşteri kaydınız başarıyla oluşturulmuştur. ");
            messageText.append("Siparişlerinizin durumu hakkında email ile bilgilendirileceksiniz.\n\n");
            messageText.append("Kaliteli hizmetimizle sizlere en iyi ürünleri sunmaya devam edeceğiz.\n\n");
            messageText.append("İyi günler dileriz,\n");
            messageText.append("Erdal Güda Terzilik\n");
            messageText.append("Telefon: +90 555 555 55 55\n");
            messageText.append("Email: info@erdalguda.com");
            
            message.setText(messageText.toString());
            mailSender.send(message);
            
            log.info("Hoş geldin emaili gönderildi: {} {}, Email: {}", firstName, lastName, email);
            
        } catch (Exception e) {
            log.error("Hoş geldin emaili gönderilirken hata: {}", e.getMessage());
        }
    }
} 