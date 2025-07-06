import requests
import os
import re
from xml.etree.ElementTree import Element, SubElement, tostring
from xml.dom import minidom

def download_svg(url, filename):
    """Скачивает SVG файл по URL"""
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        
        with open(f"{filename}.svg", 'w', encoding='utf-8') as f:
            f.write(response.text)
        
        print(f"✓ Скачан: {filename}.svg")
        return response.text
    except requests.RequestException as e:
        print(f"✗ Ошибка при скачивании {filename}: {e}")
        return None

def svg_to_android_xml(svg_content, filename):
    """Конвертирует SVG в Android Vector Drawable XML"""
    try:
        # Извлекаем основные атрибуты из SVG
        width_match = re.search(r'width="([^"]*)"', svg_content)
        height_match = re.search(r'height="([^"]*)"', svg_content)
        viewbox_match = re.search(r'viewBox="([^"]*)"', svg_content)
        
        # Создаем корневой элемент vector
        vector = Element('vector')
        vector.set('xmlns:android', 'http://schemas.android.com/apk/res/android')
        
        # Устанавливаем размеры
        if width_match and height_match:
            width = width_match.group(1)
            height = height_match.group(1)
            # Конвертируем в dp если указаны пиксели
            width = width.replace('px', 'dp')
            height = height.replace('px', 'dp')
            vector.set('android:width', width)
            vector.set('android:height', height)
        else:
            vector.set('android:width', '24dp')
            vector.set('android:height', '24dp')
        
        # Устанавливаем viewBox
        viewport_width = '24'
        viewport_height = '24'
        if viewbox_match:
            viewbox = viewbox_match.group(1)
            viewport_width = viewbox.split()[2]
            viewport_height = viewbox.split()[3]
        
        vector.set('android:viewportWidth', viewport_width)
        vector.set('android:viewportHeight', viewport_height)
        
        # Проверяем, нужно ли переворачивать фигуру (для черных фигур)
        is_black_piece = 'black' in filename.lower()
        
        # Создаем группу для трансформации (если нужно переворачивать)
        if is_black_piece:
            group = SubElement(vector, 'group')
            # Поворачиваем на 180 градусов относительно центра
            center_x = float(viewport_width) / 2
            center_y = float(viewport_height) / 2
            group.set('android:rotation', '180')
            group.set('android:pivotX', str(center_x))
            group.set('android:pivotY', str(center_y))
            parent_element = group
        else:
            parent_element = vector
        
        # Извлекаем пути из SVG
        path_matches = re.findall(r'<path[^>]*d="([^"]*)"[^>]*(?:fill="([^"]*)")?[^>]*/?>', svg_content)
        
        if not path_matches:
            # Если нет путей, попробуем найти другие элементы
            print(f"⚠ Не найдены пути в SVG для {filename}, создаем базовый XML")
            path_elem = SubElement(parent_element, 'path')
            path_elem.set('android:pathData', 'M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2z')
            path_elem.set('android:fillColor', '#FF000000')
        else:
            # Добавляем все найденные пути
            for i, (path_data, fill_color) in enumerate(path_matches):
                path_elem = SubElement(parent_element, 'path')
                path_elem.set('android:pathData', path_data)
                
                # Устанавливаем цвет заливки
                if fill_color and fill_color != 'none':
                    path_elem.set('android:fillColor', fill_color)
                else:
                    path_elem.set('android:fillColor', '#FF000000')
        
        # Форматируем XML
        rough_string = tostring(vector, 'unicode')
        reparsed = minidom.parseString(rough_string)
        pretty_xml = reparsed.toprettyxml(indent="    ")
        
        # Убираем лишние пустые строки
        pretty_xml = '\n'.join([line for line in pretty_xml.split('\n') if line.strip()])
        
        # Сохраняем в файл
        with open(f"{filename}.xml", 'w', encoding='utf-8') as f:
            f.write(pretty_xml)
        
        rotation_note = " (повернута на 180°)" if is_black_piece else ""
        print(f"✓ Конвертирован: {filename}.xml{rotation_note}")
        return True
        
    except Exception as e:
        print(f"✗ Ошибка при конвертации {filename}: {e}")
        return False

def main():
    # Словарь с именами фигур и их URL
    # Используем бесплатные SVG иконки с Wikimedia Commons
    piece_urls = {
        "white_pawn": "https://upload.wikimedia.org/wikipedia/commons/4/45/Chess_plt45.svg",
        "white_knight": "https://upload.wikimedia.org/wikipedia/commons/7/70/Chess_nlt45.svg",
        "white_bishop": "https://upload.wikimedia.org/wikipedia/commons/b/b1/Chess_blt45.svg",
        "white_rook": "https://upload.wikimedia.org/wikipedia/commons/7/72/Chess_rlt45.svg",
        "white_king": "https://upload.wikimedia.org/wikipedia/commons/4/42/Chess_klt45.svg",
        "white_queen": "https://upload.wikimedia.org/wikipedia/commons/1/15/Chess_qlt45.svg",
        "black_pawn": "https://upload.wikimedia.org/wikipedia/commons/c/c7/Chess_pdt45.svg",
        "black_knight": "https://upload.wikimedia.org/wikipedia/commons/e/ef/Chess_ndt45.svg",
        "black_bishop": "https://upload.wikimedia.org/wikipedia/commons/9/98/Chess_bdt45.svg",
        "black_rook": "https://upload.wikimedia.org/wikipedia/commons/f/ff/Chess_rdt45.svg",
        "black_king": "https://upload.wikimedia.org/wikipedia/commons/f/f0/Chess_kdt45.svg",
        "black_queen": "https://upload.wikimedia.org/wikipedia/commons/4/47/Chess_qdt45.svg"
    }
    
    # Создаем папки для сохранения
    os.makedirs("svg_files", exist_ok=True)
    os.makedirs("android_drawable", exist_ok=True)
    
    print("Начинаем скачивание и конвертацию шахматных фигур...")
    print("=" * 50)
    
    success_count = 0
    total_count = len(piece_urls)
    
    for piece_name, url in piece_urls.items():
        print(f"\nОбрабатываем: {piece_name}")
        
        # Скачиваем SVG
        svg_content = download_svg(url, f"svg_files/{piece_name}")
        
        if svg_content:
            # Конвертируем в Android XML
            if svg_to_android_xml(svg_content, f"android_drawable/{piece_name}"):
                success_count += 1
    
    print("\n" + "=" * 50)
    print(f"Готово! Успешно обработано: {success_count}/{total_count}")
    print(f"SVG файлы сохранены в папке: svg_files/")
    print(f"Android XML файлы сохранены в папке: android_drawable/")
    
    if success_count < total_count:
        print(f"\n⚠ Некоторые файлы не удалось обработать. Проверьте интернет-соединение.")

if __name__ == "__main__":
    main()