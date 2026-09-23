import csv
import sys
import os

def main():
    input_file = 'words_master.csv'
    output_file = '01_curated.csv'
    
    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    words = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for i, row in enumerate(reader):
            if i < 180:
                continue
            if i >= 360:
                break
            
            # The file has Rank, Verb
            verb = row.get('Verb', '').strip()
            rank = row.get('Rank', str(i+1)).strip()
            
            if not verb:
                continue

            words.append({
                'word': verb,
                'pos': 'verb',
                'exam_tag': 'GENERAL',
                'exam_category': 'GENERAL',
                'difficulty': 3,
                'frequency_rank': rank
            })

    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=['word', 'pos', 'exam_tag', 'exam_category', 'difficulty', 'frequency_rank'])
        writer.writeheader()
        writer.writerows(words)

    print(f"Successfully curated {len(words)} words into {output_file}.")

if __name__ == '__main__':
    main()
