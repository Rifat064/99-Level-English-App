import csv
import sys
import os

def main():
    input_file = '01_curated.csv'
    output_file = '02_paired.csv'

    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    words = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            words.append(row)

    if len(words) < 180:
        print(f"Warning: Expected 180 words, found {len(words)}.")

    paired = []
    # Simple sequential pairing for now
    for i in range(0, len(words) - 1, 2):
        w1 = words[i]
        w2 = words[i+1]
        
        paired.append({
            'word_a': w1['word'],
            'word_b': w2['word'],
            'word_a_rank': w1['frequency_rank'],
            'word_b_rank': w2['frequency_rank'],
            'pos': w1['pos'], # assuming same pos
            'exam_tag': w1['exam_tag'],
            'exam_category': w1['exam_category'],
            'difficulty': w1['difficulty']
        })

    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        fieldnames = ['word_a', 'word_b', 'word_a_rank', 'word_b_rank', 'pos', 'exam_tag', 'exam_category', 'difficulty']
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(paired)

    print(f"Successfully paired {len(words)} words into {len(paired)} duos in {output_file}.")

if __name__ == '__main__':
    main()
