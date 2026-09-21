import csv
import sys
import os
import argparse

def mock_llm_call(word_a, word_b):
    return {
        'sentence_en': f"The person tried to {word_a} but ended up having to {word_b}.",
        'image_prompt': f"A flat vector illustration showing someone trying to {word_a} and {word_b}."
    }

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--mock', action='store_true', help="Use mock LLM generation")
    args = parser.parse_args()

    input_file = '02_paired.csv'
    output_file = '03_sentences.csv'

    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    pairs = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            pairs.append(row)

    sentences = []
    for pair in pairs:
        if args.mock:
            llm_result = mock_llm_call(pair['word_a'], pair['word_b'])
        else:
            # TODO: Implement real LLM call here using OpenAI or Gemini
            # For now, default to mock if real is not implemented
            print("Real LLM not fully configured, falling back to mock.")
            llm_result = mock_llm_call(pair['word_a'], pair['word_b'])
        
        pair['sentence_en'] = llm_result['sentence_en']
        pair['image_prompt'] = llm_result['image_prompt']
        sentences.append(pair)

    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        if not sentences:
            return
        fieldnames = list(sentences[0].keys())
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(sentences)

    print(f"Successfully generated {len(sentences)} sentences into {output_file}.")

if __name__ == '__main__':
    main()
