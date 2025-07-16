package com.android.cornleafdetection.data

import com.android.cornleafdetection.R

object DiseaseData {
    fun getDiseaseList(): List<CornLeafDisease> {
        return listOf(
            CornLeafDisease(
                name = "Bercak Daun",
                scientificName = "Helminthosporium turcicum",
                description = "Penyakit ini menyebabkan bercak memanjang berwarna cokelat keabu-abuan pada daun jagung, yang dapat mengganggu proses fotosintesis dan mengurangi hasil panen.",
                symptoms = "- Muncul bercak lonjong berwarna coklat keabu-abuan pada daun.\n" +
                        "- Bercak kecil berbentuk oval yang basah pada daun jagung \n" +
                        "- Bercak memanjang berbentuk elips dan bercak menjadi kering meluas \n" +
                        "- Warna gejala tersebut hijau keabu-abuan atau coklat \n" +
                        "- Lesi panjang (hingga 6 inci), elips hingga berbentuk cerutu, berwarna abu-abu kehijauan yang berubah menjadi cokelat kecokelatan",
                cause = "Disebabkan oleh jamur Helminthosporium turcicum. Suhu sedang, cuaca lembab, dan kurangnya pengolahan lahan pertanian setelah panen \n" +
                        "Jamur ini dapat bertahan dalam sisa-sisa tanaman yang terinfeksi atau dalam tanah selama 2- 7 tahun.",
                treatment = "Gunakan varietas jagung yang tahan terhadap penyakit bercak daun.\n" +
                        "Lakukan rotasi tanaman dan sanitasi lahan untuk mengurangi sumber inokulum.\n" +
                        "Aplikasikan fungisida preventif sesuai anjuran.",
                imageResId = R.drawable.bercak_daun,
            ),
            CornLeafDisease(
                name = "Bulai Daun",
                scientificName = "Peronosclerospora maydis",
                description = "Bulai adalah penyakit sistemik yang menyerang tanaman jagung sejak dini, menyebabkan daun belang dan pertumbuhan kerdil. Penyakit ini dapat menyebar melalui benih dan tanah.",
                symptoms = "Daun menunjukkan klorosis (belang-belang kuning atau putih) sejajar tulang daun, pertumbuhan tanaman terhambat, dan pada kondisi lembap, muncul lapisan putih seperti tepung pada permukaan daun.",
                cause = "Infeksi oleh jamur Peronosclerospora maydis.\nJamur ini menyebar melalui biji dan tanah, terutama pada kondisi lembap.",
                treatment = "Gunakan benih jagung yang tahan terhadap bulai.\nLakukan pengolahan tanah yang baik dan rotasi tanaman.\nAplikasikan fungisida sistemik sebagai perlakuan benih.\nGunakan biopestisida berbahan aktif Bacillus subtilis.\nGunakan fungisida sistemik berbahan aktif metalaksil.",
                imageResId = R.drawable.bulai_daun
            ),
            CornLeafDisease(
                name = "Hawar Daun",
                scientificName = "Bipolaris maydis",
                description = "Hawar daun menyebabkan kerusakan besar dengan memunculkan bercak yang cepat membesar dan merusak jaringan daun. Kondisi lembap mempercepat penyebarannya.",
                symptoms = "Muncul bercak kecil berwarna coklat muda sampai abu-abu.\nBentuk bercak biasanya oval atau elips memanjang, sejajar dengan tulang daun.\nUkuran bercak bisa 2–15 cm panjangnya.\nDaun bisa menjadi kuning, mengering, lalu mati jika infeksi parah.",
                cause = "Jamur Bipolaris maydis menyerang jaringan daun melalui angin dan air hujan, berkembang pesat pada kondisi lembap.",
                treatment = "Gunakan Trichoderma harzianum untuk menghambat pertumbuhan patogen.\nAplikasikan fungisida berbahan aktif propineb.\nLakukan rotasi tanaman dan sanitasi lahan untuk mengurangi sumber inokulum.",
                imageResId = R.drawable.hawar_daun
            ),
            CornLeafDisease(
                name = "Karat Daun",
                scientificName = "Puccinia sorghi",
                description = "Karat daun ditandai dengan munculnya pustula berwarna oranye hingga cokelat pada daun. Serangan berat dapat menyebabkan daun mengering dan mengurangi hasil panen.",
                symptoms = "Muncul bintik-bintik kecil berwarna cokelat hingga merah oranye pada kedua sisi daun, yang berkembang menjadi pustula berisi spora.",
                cause = "Disebabkan oleh jamur Puccinia sorghi. Jamur Puccinia sorghi menyebar melalui spora di udara, terutama pada kondisi lembap.",
                treatment = "Tanam varietas jagung yang tahan terhadap karat daun.\nLakukan rotasi tanaman dan buang sisa tanaman yang terinfeksi.\nGunakan fungisida spesifik untuk karat daun sesuai rekomendasi.\nSemprotkan ekstrak neem untuk menghambat perkembangan jamur.\nGunakan fungisida berbahan aktif azoksistrobin atau propikonazol.",
                imageResId = R.drawable.karat_daun
            )
        )
    }
}
